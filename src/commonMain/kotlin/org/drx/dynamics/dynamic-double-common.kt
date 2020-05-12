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


operator fun Dynamic<Double>.plus(other: Dynamic<Double>): Dynamic<Double> {
    val result by Dynamic(value + other.value)
    subscribe(this::class){
        result.value = it + other.value
    }
    other.subscribe(other::class){
        result.value = value + it
    }
    return result
}

operator fun Dynamic<Double>.minus(other: Dynamic<Double>): Dynamic<Double> {
    val result by Dynamic(value - other.value)
    subscribe(this::class){
        result.value = it - other.value
    }
    other.subscribe(other::class){
        result.value = value - it
    }
    return result
}

operator fun Dynamic<Double>.times(other: Dynamic<Double>): Dynamic<Double> {
    val result by Dynamic(value * other.value)
    subscribe(this::class){
        result.value = it * other.value
    }
    other.subscribe(other::class){
        result.value = value * it
    }
    return result
}

operator fun Dynamic<Double>.div(other: Dynamic<Double>): Dynamic<Double> {
    val result by Dynamic(value / other.value)
    subscribe(this::class){
        result.value = it / other.value
    }
    other.subscribe(other::class){
        result.value = value / it
    }
    return result
}