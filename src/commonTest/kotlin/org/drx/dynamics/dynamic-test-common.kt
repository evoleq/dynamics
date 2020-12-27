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
package org.drx.dynamics

import kotlinx.coroutines.delay
import org.evoleq.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DynamicTestCommon {
    @Test
    fun dynamic() = runTest{
        class Dynamic
        val dynamic by Dynamic(0)
        dynamic.subscribe(Dynamic::class){
            println("value = $it")
        }

        dynamic.value = 1
        class Pushed
        val pushed = dynamic.push(Pushed::class){x -> 2*x}
        delay(50)
        dynamic.value = 2
        delay(100)
        assertTrue(pushed.value == 4)
        dynamic.unsubscribe(Pushed::class)
        dynamic.value = 3
        assertTrue(pushed.value == 4)
    }

}