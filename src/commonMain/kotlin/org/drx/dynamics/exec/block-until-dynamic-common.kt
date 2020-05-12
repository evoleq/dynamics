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
import org.drx.dynamics.markers.EvoleqDsl


/**
 * Block coroutine execution until a dynamic has the right value
 */
class Release
@EvoleqDsl
suspend fun <T : Any> blockUntil(dynamic: Dynamic<T>, predicate: (T)-> Boolean) =
    if(!predicate(dynamic.value)) {
        try {
            val eternity: Deferred<Unit> = CoroutineScope(Job()).async {
                delay(Long.MAX_VALUE)
            }

            dynamic.subscribe(Release::class) {
                if(predicate(it)) {
                    dynamic.unsubscribe(Release::class)
                    eternity.cancel()
                }
            }
            if(!predicate(dynamic.value)) {
                eternity.await()
            } else {
                dynamic.unsubscribe(Release::class)
                eternity.cancel()
            }
        } catch (exception: Exception) { }
    }  else { Unit }

