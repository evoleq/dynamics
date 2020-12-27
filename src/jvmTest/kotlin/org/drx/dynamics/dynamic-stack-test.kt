package org.drx.dynamics

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DynamicStackTest {
    @Test fun basics() = runBlocking{
        val stack = Stack.Empty
        val x = stack.push(1).push(2)//.push(3)
        println(x)
        println(x.pop())
        println(x.bottom())
        delay(1_000)



/*
        val dynStack by DynamicStack<Int>()

        (1..1000).forEach {
            dynStack.push(it)
        }
*/


    }
}