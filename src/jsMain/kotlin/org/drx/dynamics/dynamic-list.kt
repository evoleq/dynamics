package org.drx.dynamics

import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.drx.dynamics.exec.blockUntil
import org.drx.dynamics.exec.blockWhileEmpty
import kotlin.reflect.KProperty

open class DynamicArrayList<T>(private val initialValue: ArrayList<T>) : Dynamic<ArrayList<T>>(initialValue) {


    //private val dynamicListContext = newSingleThreadContext("dynymic-list-context")
    private val mutex = Mutex()

    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicArrayList<T> = this

    private lateinit var sizeIn: Dynamic<Int>// = Dynamic(0)
    val size by lazy {
        scope.launch {
            sizeIn = push(SizeId::class) {
                initialValue.size
            }
        }
        while(!::sizeIn.isInitialized) {
            window.setTimeout(
                {},1
            )
        }
        sizeIn
    }
    private lateinit var isEmptyIn: Dynamic<Boolean>// = Dynamic(true)
    val isEmpty by lazy {
        scope.launch {
            isEmptyIn = push(IsEmpty::class) { initialValue.isEmpty() }
        }
        while(!::isEmptyIn.isInitialized) {
            window.setTimeout(
                {},1
            )
        }
        isEmptyIn
    }

    val isNotEmpty by lazy{ !isEmpty }

    suspend fun contains(item: T) = push(Contains::class){it.contains(item)}
    suspend fun contains(dynamicItem: Dynamic<T>) = push(ContainsDynamic::class){it.contains(dynamicItem.value)}
    //fun containsAll(elements: DynamicCollection<T>) = push(ContainsAllDynamic::class){it.containsAll(elements.value)}



    suspend fun add(item: T): Boolean = coroutineScope{
        withContext(Dispatchers.Default) {
            mutex.withLock {
                initialValue.add(item)
                value = initialValue
                true
            }
        }
    }
    suspend fun add(index: Int, item: T): Boolean = coroutineScope{
        withContext(Dispatchers.Default) {
            mutex.withLock {
                initialValue.add(index, item)
                value = initialValue
                true
            }
        }
    }
    suspend fun pop(): T = coroutineScope{
        withContext(Dispatchers.Default) {
            mutex.withLock {
                with(initialValue.first()) {
                    initialValue.removeAt(0)
                    value = arrayListOf(*initialValue.map { it }.toTypedArray())//value.drop(1)
                    this
                }
            }
        }
    }
    // todo("Implement array-list and list-methods)

    inline fun <reified S> map(noinline f: (T)->S): DynamicArrayList<S> =
        DynamicArrayList(arrayListOf(*value.map(f).toTypedArray()))
}


suspend fun <S,T> DynamicArrayList<S>.onNext(action: suspend CoroutineScope.(S)->T): T = with( scope ) {
    blockWhileEmpty()
    val oldSize = size.value
    //println("list-size = $oldSize")
    with(pop()) {
          blockUntil(size){it < oldSize}
        action(this)
    }
}