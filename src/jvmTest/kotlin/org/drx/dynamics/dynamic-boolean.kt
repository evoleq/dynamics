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

class DynamicBooleanTest{
    @Test fun and() = runBlocking {
        val a by DynamicBoolean(true)
        val b by DynamicBoolean(true)
        val c by a and b

        assert (c.value)
        a.value = false
        delay(10)
        assert(!c.value)
        b.value = false
        delay(10)
        assert(!c.value)
        a.value = true
        delay(10)
        assert(!c.value)
        b.value = true
        delay(10)
        assert(c.value)
    }

    @Test fun or() = runBlocking{
        val a by DynamicBoolean(true)
        val b by DynamicBoolean(true)
        val c by a or b

        assert (c.value)
        a.value = false
        delay(10)
        assert(c.value)
        b.value = false
        delay(10)
        assert(!c.value)
        a.value = true
        delay(10)
        assert(c.value)

        b.value = true
        delay(10)
        assert(c.value)
    }

    @Test fun xor() = runBlocking {
        val a by DynamicBoolean(true)
        val b by DynamicBoolean(true)
        val c by a xor b

        assert (!c.value)
        a.value = false
        delay(10)
        assert(c.value)
        b.value = false
        delay(10)
        assert(!c.value)
        a.value = true
        delay(10)
        assert(c.value)
        b.value = true
        delay(10)
        assert(!c.value)
    }

    @Test fun nor() = runBlocking {
        val a by DynamicBoolean(true)
        val b by DynamicBoolean(true)
        val c by a nor b

        assert (!c.value)
        a.value = false
        delay(10)
        assert(!c.value)
        b.value = false
        delay(10)
        assert(c.value)
        a.value = true
        delay(10)
        assert(!c.value)
        b.value = true
        delay(10)
        assert(!c.value)
    }


    @Test fun not() = runBlocking {
        val a by DynamicBoolean(true)
        val c by !a

        assert (!c.value)
        a.value = false
        delay(10)
        assert(c.value)
        a.value = true
        delay(10)
        assert(!c.value)

    }
}