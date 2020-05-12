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
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DynamicArrayListTest {
    @Test fun list() = runBlocking {
        val list = arrayListOf<Int>()
        val dynamicList by DynamicArrayList(list)
        val containsOne by dynamicList.contains(1)
        val containsOneAndTwo by dynamicList.containsAll(DynamicArrayList(arrayListOf(1,2)))
        assert(dynamicList.isEmpty.value)
        assert(!containsOne.value)
        assert(!containsOneAndTwo.value)
        dynamicList.add(1)
        delay(1_000)

        assert(dynamicList.value.isNotEmpty())
        assert(containsOne.value)
        assert(!dynamicList.isEmpty.value)
        assert(dynamicList.isNotEmpty.value)
        assert(!containsOneAndTwo.value)
        dynamicList+2
        delay(10)
        assert(containsOneAndTwo.value)

    }
}