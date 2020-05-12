package org.drx.dynamics

import kotlinx.coroutines.CoroutineScope
import org.drx.dynamics.exec.blockUntil
import org.drx.dynamics.exec.blockWhileEmpty
import kotlin.reflect.KProperty

open class DynamicArrayList<T>(private val initialValue: ArrayList<T>) : Dynamic<ArrayList<T>>(initialValue) {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicArrayList<T> = this

    val size by lazy { push(SizeId::class){it.size} }
    val isEmpty by lazy { push(IsEmpty::class){it.isEmpty()} }
    val isNotEmpty by lazy{ !isEmpty }

    fun contains(item: T) = push(Contains::class){it.contains(item)}
    fun contains(dynamicItem: Dynamic<T>) = push(ContainsDynamic::class){it.contains(dynamicItem.value)}
    //fun containsAll(elements: DynamicCollection<T>) = push(ContainsAllDynamic::class){it.containsAll(elements.value)}



    fun add(item: T): Boolean {
        initialValue.add(item)
        value = initialValue
        return true
    }
    fun add(index: Int, item: T): Boolean {
        initialValue.add(index,item)
        value = initialValue
        return true
    }
    fun pop(): T = with(initialValue.first()) {
        initialValue.removeAt(0)
        value = arrayListOf(*initialValue.map{it}.toTypedArray())//value.drop(1)
        this
    }
    // todo("Implement array-list and list-methods)

    inline fun <reified S> map(noinline f: (T)->S): DynamicArrayList<S> =
        DynamicArrayList(arrayListOf(*value.map(f).toTypedArray()))
}


suspend fun <S,T> DynamicArrayList<S>.onNext(action: suspend CoroutineScope.(S)->T): T = with( scope) {
    blockWhileEmpty()
    val oldSize = size.value
    //println("list-size = $oldSize")
    with(pop()) {
          blockUntil(size){it < oldSize}
        action(this)
    }
}