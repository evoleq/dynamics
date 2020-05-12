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
package org.drx.dynamics.dsl

import kotlinx.coroutines.CoroutineScope
import org.drx.dynamics.Dynamic
import org.drx.dynamics.exec.runUntil
import org.drx.dynamics.markers.EvoleqDsl

/**
 * Auxiliary function
 */
@EvoleqDsl
fun <T> CoroutineScope.run(block: suspend CoroutineScope.()->T): suspend CoroutineScope.()->T = block


/**
 * Auxiliary function
 */
@EvoleqDsl
suspend infix fun <C: Any, T> (suspend CoroutineScope.()->T).until(condition: Pair<Dynamic<C>, (C)->Boolean>): T?
        = runUntil(condition.first,condition.second,this)

/**
 * Auxiliary function
 */
@EvoleqDsl
suspend infix fun <C: Any, T> (suspend CoroutineScope.()->T).asLongAs(condition: Pair<Dynamic<C>, (C)->Boolean>): T?
        = runUntil(condition.first,{c -> !condition.second(c)},this)

/**
 * Auxiliary function
 */
@EvoleqDsl
infix fun <C:Any> Dynamic<C>.fulfills(predicate: (C) -> Boolean): Pair<Dynamic<C>, (C)->Boolean> = Pair(this,predicate)


/**
 * Auxiliary function
 */
@EvoleqDsl
fun Dynamic<Boolean>.isTrue(): Pair<Dynamic<Boolean>, (Boolean) -> Boolean> = this fulfills { x -> x }

/**
 * Auxiliary function
 */
@EvoleqDsl
fun Dynamic<Boolean>.isFalse(): Pair<Dynamic<Boolean>, (Boolean) -> Boolean> = this fulfills { x -> !x }
