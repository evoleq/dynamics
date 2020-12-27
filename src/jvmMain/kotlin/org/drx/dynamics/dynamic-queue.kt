package org.drx.dynamics

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.drx.dynamics.exec.blockUntil
import java.util.*
import kotlin.NoSuchElementException
import kotlin.reflect.KProperty
import kotlin.time.Duration

open class DynamicQueue<T>(
    private val initialValue: Queue<T> = LinkedList()
): Dynamic<Queue<T>>(
    initialValue
) {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicQueue<T> = this

    private val dynamicListContext = newSingleThreadContext("dynamic-queue-context")
    //private val mutex = Mutex()

    val isEmpty by Dynamic(initialValue.isEmpty())

    suspend fun add(item: T): DynamicQueue<T> = coroutineScope {
        //withContext(dynamicListContext) {
        //    mutex.withLock {
                val size = value.size
                value.add(item)
                while(value.size < size + 1){
                    delay(1)
                }
                isEmpty.value = false
                this@DynamicQueue
        //    }
        }


    suspend fun pop(): T? = coroutineScope {  //withContext(dynamicListContext) {
        value.poll()
        //mutex.withLock {
        /*
            when(isEmpty.value && value.isEmpty()) {
                true -> throw NoSuchElementException()
                false -> {
                    //isEmpty.value = value.size > 1
                    val item = value.remove()
                    isEmpty.value = value.isEmpty()
                    item
                }
            }

         */
       // }
    }
}

suspend fun <S, T> DynamicQueue<S>.onNext(action: suspend CoroutineScope.(S)->T): T? = with(scope) {
    blockUntil(isEmpty){v -> !v}
    with(pop()) {
        when(this){
            null -> null
            else -> action(this)
        }
    }
}