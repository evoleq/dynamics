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

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.drx.dynamics.markers.EvoleqDsl
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

typealias ID = KClass<*>

open class Dynamic<T>(private val initialValue : T, val scope: CoroutineScope = CoroutineScope(Job())) : ReadOnlyProperty<Any?, Dynamic<T>> {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): Dynamic<T> = this

    val mutex = Mutex()
    val context = Dispatchers.Unconfined

    private val  prop : ReadWriteProperty<Any?, T>
    private val subscriptions = arrayListOf<Pair<ID,suspend (T)->Unit>>()
    init {
        prop = Delegates.observable(initialValue) { property, oldValue, newValue ->
            //if(newValue != oldValue) {
                scope.launch {
                    withContext(context) {
                        mutex.withLock {
                            subscriptions.asSequence().forEach {
                                scope.launch {
                                    //coroutineScope {
                                    it.second(newValue)
                                    //}
                                }
                            }
                        }
                    }
                }
            //}
        }
    }

    //fun value(): ReadWriteProperty<Any?, T> = prop
    var value: T by prop


    fun subscribe(id: ID, onNext: suspend (T)->Unit) {
        scope.launch {
            withContext(context) {
                mutex.withLock {
                    subscriptions.add(id to onNext)
                }
            }
        }
    }
    fun unsubscribe(id: ID) {
        scope.launch {
            withContext(context) {
                mutex.withLock {
                    val toRemove = subscriptions.find { it.first == id }
                    subscriptions.remove(toRemove) //{ it.first == id }
                }
            }
        }
    }
    suspend fun<S> push(id: ID, scope: CoroutineScope = CoroutineScope(Job()), f: suspend (T)->S ): Dynamic<S> {
        val dynamic by Dynamic(f(value),scope)
        subscribe(id){
            scope.launch {
                dynamic.value = f(it)
            }
        }
        return dynamic
    }

}

fun <T> CoroutineScope.dynamic(initialValue : T) = Dynamic(initialValue,this)
class IsNotNull
@EvoleqDsl
suspend fun <T> Dynamic<T?>.isNotNull(): Dynamic<Boolean> = push(IsNotNull::class) {
    it != null
}


/*
infix fun <S, T> Dynamic<S>.map(f: (S)->T): Dynamic<T> = Dynamic(f(value))
fun <T> Dynamic<Dynamic<T>>.mu(): Dynamic<T> = value

interface KlDynamic<S,T> : (S)->Dynamic<T>
@Suppress("FunctionName")
fun <S, T> KlDynamic(arrow: (S)->Dynamic<T>): KlDynamic<S, T> = object : KlDynamic<S, T> {
    override fun invoke(p1: S): Dynamic<T> = arrow(p1)
}
operator fun <R,S,T> KlDynamic<R, S>.times(other: KlDynamic<S, T>): KlDynamic<R, T> = with(KlDynamic{
    r: R -> (this@times(r) map other).mu()
}) {

    this
}



fun <S, T> Dynamic<S>.bind(other: KlDynamic<S, T>): Dynamic<T> =
    with(KlDynamic<S,S> { s ->
        this@bind.value = s
        this@bind
    } * other) {
        this(this@bind.value)
    }
*/