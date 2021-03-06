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
import org.drx.dynamics.DynamicArrayList
import org.drx.dynamics.DynamicBoolean
import org.junit.Test
import kotlin.system.measureTimeMillis

class BlockUntilDynamicTestJvm {


    @Test
    fun blockUntilDynamic() = runBlocking {
        val bool by DynamicBoolean(false)
        val delay = 1_000L
        CoroutineScope(Job()).async { coroutineScope {
            delay(delay)
            bool.value = true
        } }
        var time = System.currentTimeMillis()
        blockUntil(bool) { value -> value }
        time = System.currentTimeMillis() - time
        assert(time > delay)

    }
    @Test
    fun blockWhileEmptyDynamic() = runBlocking {
        val list = DynamicArrayList<Int>(arrayListOf())
        val delay = 1_000L
        CoroutineScope(Job()).async { coroutineScope {
            delay(delay)
            list.add(1)
        } }
        val time = measureTimeMillis {
            list.blockWhileEmpty()
        }
        assert(time >= delay)
        assert(list.value.contains(1))
    }
}