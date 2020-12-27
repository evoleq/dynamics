package org.drx.dynamics

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.drx.dynamics.exec.blockUntilInitializationOf
import org.junit.Test
import kotlin.test.assertEquals

class SingletonTest {
    @Test fun basics() {
        val s by Singleton<Int>()

        s.put(1)
        s.put(2)

        assertEquals(1,s.value)
    }
    @Test fun `on init`() = runBlocking{
        val singleton by Singleton<Int>()
        val value = 0
        val delayTime = 1_000L
        val startTime = System.currentTimeMillis()

        val assertJob = GlobalScope.launch {
            singleton.onInit {
                x ->
                    val measuredTime = System.currentTimeMillis() - startTime
                    assert(x == value)
                    assert(measuredTime >= delayTime)
            }
        }

        val putJob = GlobalScope.launch {
            delay(delayTime)
            singleton.put{ value }
        }

        assertJob.join()
    }

    @Test fun `block until initialization`() = runBlocking{
        val singleton by Singleton<Int>()
        val value = 0
        val delayTime = 1_000L
        val startTime = System.currentTimeMillis()

        val assertJob = GlobalScope.launch {
            blockUntilInitializationOf(singleton)
            val x = singleton.value
            val measuredTime = System.currentTimeMillis() - startTime
            assert(x == value)
            assert(measuredTime >= delayTime)

        }

        val putJob = GlobalScope.launch {
            delay(delayTime)
            singleton.put{ value }
        }

        assertJob.join()
    }
}