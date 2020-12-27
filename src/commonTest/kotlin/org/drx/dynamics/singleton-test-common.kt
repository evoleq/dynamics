package org.drx.dynamics

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.dynamics.exec.blockUntilInitializationOf
import org.evoleq.test.runTest
import org.evoleq.test.time
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SingletonTestCommon {
    @Test
    fun basics() = runTest{
        val s by Singleton<Int>()

        s.put(1)
        s.put(2)

        assertEquals(1,s.value)
    }
    @Test fun onInit() = runTest{
        val singleton by Singleton<Int>()
        val value = 0
        val delayTime = 1_000L
        val startTime = time()

        val assertJob = GlobalScope.launch {
            singleton.onInit {
                x ->
                    val measuredTime = time() - startTime
                    assertTrue(x == value)
                    assertTrue(measuredTime >= delayTime)
            }
        }

        val putJob = GlobalScope.launch {
            delay(delayTime)
            singleton.put{ value }
        }

        assertJob.join()
    }

    @Test fun blockUntilInitialization() = runTest{
        val singleton by Singleton<Int>()
        val value = 0
        val delayTime = 1_000L
        val startTime = time()

        val assertJob = GlobalScope.launch {
            blockUntilInitializationOf(singleton)
            val x = singleton.value
            val measuredTime = time() - startTime
            assertTrue(x == value)
            assertTrue(measuredTime >= delayTime)

        }

        val putJob = GlobalScope.launch {
            delay(delayTime)
            singleton.put{ value }
        }

        assertJob.join()
    }
}