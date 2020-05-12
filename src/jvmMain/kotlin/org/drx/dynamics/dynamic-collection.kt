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

import kotlin.reflect.KProperty

abstract class DynamicCollection<T>(collection: Collection<T>) : Dynamic<Collection<T>>(collection) {
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicCollection<T> {
        return this
    }
    val size by lazy { push(SizeId::class){it.size} }
    val isEmpty by lazy { push(IsEmpty::class){it.isEmpty()} }
    val isNotEmpty by lazy{ !isEmpty }

    fun contains(item: T) = push(Contains::class){it.contains(item)}
    fun contains(dynamicItem: Dynamic<T>) = push(ContainsDynamic::class){it.contains(dynamicItem.value)}
    fun containsAll(elements: DynamicCollection<T>) = push(ContainsAllDynamic::class){it.containsAll(elements.value)}

    operator fun plus(item: T){
        value+=item
    }
    operator fun minus(item: T) {
        value-=item
    }

    init{

    }
}