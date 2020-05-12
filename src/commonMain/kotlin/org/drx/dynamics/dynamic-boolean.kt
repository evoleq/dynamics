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

import org.drx.dynamics.markers.EvoleqDsl




@EvoleqDsl
infix fun Dynamic<Boolean>.and(other: Dynamic<Boolean>): Dynamic<Boolean> {
    val and by Dynamic(value && other.value)
    subscribe(this::class){
        and.value = it && other.value
    }
    other.subscribe(other::class){
        and .value = value && it
    }
    return and
}

@EvoleqDsl
infix fun Dynamic<Boolean>.or(other: Dynamic<Boolean>): Dynamic<Boolean> {
    val or by Dynamic(value || other.value)
    subscribe(this::class){
        or.value = it || other.value
    }
    other.subscribe(other::class){
        or.value = value || it
    }
    return or
}
@EvoleqDsl
infix fun Dynamic<Boolean>.xor(other: Dynamic<Boolean>): Dynamic<Boolean> = (this and !other) or (!this and other)
@EvoleqDsl
infix fun Dynamic<Boolean>.nor(other: Dynamic<Boolean>): Dynamic<Boolean> = !(this or other)

@EvoleqDsl
operator fun Dynamic<Boolean>.not(): Dynamic<Boolean> {
    val not by Dynamic(!value)
    subscribe(not::class) {
        not.value = !it
    }
    return not
}