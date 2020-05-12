/**
 * Copyright (c) 2020 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drx.dynamics.exec

import kotlinx.coroutines.*
import org.drx.dynamics.Dynamic
import org.junit.Test
import kotlin.system.measureTimeMillis

class RunUntilDynamicTest {

    @Test
    fun runUntil() = runBlocking {
        val condition by Dynamic("")
        CoroutineScope(Job()).async { coroutineScope {
            delay(1_000)
            condition.value = "SET"
        }}
        var result:Unit? = null
        var time = measureTimeMillis {
            result = runUntil(condition, { s -> s == "SET" }) {
                delay(10_000)
            }
        }
        println(time)
        assert(time < 2_100)
        assert(result == null)
        var result1: Unit? = null
        CoroutineScope(Job()).async { coroutineScope {
            delay(1_500)
            condition.value = "SET_1"
        }}
        val time1 = measureTimeMillis {

            result1 = runUntil(condition, { s -> s == "SET_1" }) {
                delay(1_000)
                Unit
            }
        }
        println(time1)
        assert(time1>=1_000)
        assert(result1 == Unit)
    }

    @Test fun runUntilWithDecomposedFunctions() = runBlocking {
        val dynamic by Dynamic("")
        val result = run{delay(1_000)} until dynamic.push(this::class){s -> s == "SET"}.isTrue()
        dynamic.value = "SET"
        result!!
        dynamic.value = ""
        CoroutineScope(Job()).async { coroutineScope {
            delay(1_000)
            dynamic.value = "SET"
        }}
        val result1: Int? = run{delay(10_000); 3} until dynamic.fulfills {s -> s == "SET"}
        assert(result1 == null)
    }

    @Test fun runAsLongAsDecomposedFunctions () = runBlocking {
        val condition by Dynamic(0)
        CoroutineScope(Job()).async { coroutineScope {
            delay(500)
            condition.value = 1
        }}
        val result = run { delay(1000) } asLongAs (condition fulfills { x: Int -> x <= 0 })
        assert(result == null)
    }
}