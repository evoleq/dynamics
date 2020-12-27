package org.drx.dynamics


import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DynamicQueueTest {
    @Test fun x() = runBlocking {
        val queue by DynamicQueue<Int>()

        launch{
            queue.isEmpty.subscribe(Int::class){
                println("isEmpty = $it")
                if(!it) {
                    with(queue.pop()){
                        println("popped = $this")
                    }
                }
            }
        }

        launch {
            (1..1_000).forEach {
                delay(1)
                queue.add(it)
            }
        }

delay(5_000)
    }
}