package org.drx.dynamics

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import org.drx.dynamics.exec.blockUntil
import kotlin.reflect.KProperty

sealed class Stack<out T> {
    object Empty : Stack<Nothing>()
    data class Populated<T>(
        val item: T,
        var tail: Stack<T>
    ):  Stack<T>()
}

open class DynamicStack<T>(initialValue: Stack<T> = Stack.Empty) : Dynamic<Stack<T>>(initialValue) {

    //lateinit var size: Dynamic<Int>//
    val size by Dynamic(0)//initialValue.size())

    //lateinit var isEmpty: Dynamic<Boolean>//
    val isEmpty by Dynamic(true)//initialValue.isEmpty())

    private class SizeId
    private class IsEmptyId

    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicStack<T> = this


    suspend fun push(item: T): DynamicStack<T> = coroutineScope {
        with(this@DynamicStack) {
            //withContext(context) {
                //mutex.withLock {
                    //println("pushing")
                    value = value.push(item)
                    isEmpty.value = false
                    this@DynamicStack
            //    }
            //}
        }
    }

    suspend fun pop(): Pair<T?, DynamicStack<T>> = coroutineScope {
        with(this@DynamicStack) {
            withContext(context) {
                mutex.withLock {
                    //println("popping")
                    val popped = value.pop()
                    value = popped.second
                    isEmpty.value = value.isEmpty()

                    Pair(popped.first, this@DynamicStack)
                }
            }
        }
    }


    suspend fun bottom(): Pair<T?, DynamicStack<T>> = coroutineScope {
        with(this@DynamicStack) {
            //withContext(context) {
                //mutex.withLock {
                    //println("bottom")
                    val first = value.bottom()
                    value = first.second
                    isEmpty.value = value.isEmpty()
                    Pair(first.first, this@DynamicStack)
                //}
            //}
        }
    }
}
suspend fun <S, T> DynamicStack<S>.onNext(action: suspend CoroutineScope.(S)->T): T = coroutineScope{
    blockUntil(isEmpty){v ->!v}
    with(bottom()) {
        action(first!!)
    }
}

fun <T> Stack<T>.push(item: T): Stack<T> =
    Stack.Populated(item,this)

fun <T> Stack<T>.pushAll(vararg items: T): Stack<T> = pushAll(items.toList())

tailrec fun <T> Stack<T>.pushAll(items: Collection<T>): Stack<T> = when(items.isEmpty()) {
    true -> this
    else -> push(items.first()).pushAll(items.drop(1))
}

fun <T> Stack<T>.pop(): Pair<T?, Stack<T>> = when(this) {
    is Stack.Empty -> Pair(null, this)
    is Stack.Populated -> Pair(item, tail)
}

fun <T> Stack<T>.revert(): Stack<T> = band().rightMost().left

fun <T> Stack<T>.isEmpty(): Boolean = this is Stack.Empty

fun <T> Stack<T>.size(): Int = when(this) {
    is Stack.Empty -> 0
    else -> Pair(0,this).size().first
}

tailrec fun <T> Pair<Int, Stack<T>>.size(): Pair<Int, Stack<T>> = when(val stack = second) {
    is Stack.Empty -> this
    is Stack.Populated -> Pair(first +1, stack.tail).size()
}


data class Band<T>(
    val left: Stack<T>,
    val right: Stack<T>
)

fun <T> Band<T>.right(): Band<T> =
    with(right.pop()){
        when(first) {
            null -> Band(left,Stack.Empty)
            else -> Band(left.push(first!!), second)
    }
}

tailrec fun <T> Band<T>.rightMost(): Band<T> = when(right) {
    is Stack.Empty -> this
    else -> right().rightMost()
}

fun <T> Band<T>.left(): Band<T> =
    with(left.pop()){
        when(first) {
            null -> Band(Stack.Empty,right)
            else -> Band(second, right.push(first!!))
        }
    }

tailrec fun <T> Band<T>.leftMost(): Band<T> = when(left) {
    is Stack.Empty -> this
    else -> left().leftMost()
}

fun <T> Stack<T>.band(): Band<T> = Band(Stack.Empty, this)

fun <T> Stack<T>.bottom(): Pair<T?, Stack<T>> = when(this) {
    is Stack.Empty -> Pair(null, this)
    is Stack.Populated -> when(tail) {
        is Stack.Empty -> Pair(item, tail)
        is Stack.Populated -> {

            var nextTail: Stack.Populated<T> = tail as Stack.Populated<T>
            var currentTail = this as Stack.Populated
            while (nextTail.tail != Stack.Empty) {
                currentTail  = nextTail
                nextTail = (nextTail).tail as Stack.Populated
            }
            val bottomItem = (nextTail as Stack.Populated).item
            currentTail.tail = Stack.Empty
            Pair(bottomItem, this)
        }
    }
}
    /*
    with(band().rightMost()) {
    val popped = left.pop()
    Pair(popped.first,Band(popped.second,Stack.Empty).leftMost().right)
}

     */