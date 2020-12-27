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
import org.drx.dynamics.exec.blockWhileEmpty
import org.omg.CORBA.INTERNAL
import kotlin.reflect.KProperty

abstract class DynamicList<T>(list: List<T>) : DynamicCollection<T>(list) {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicList<T> = this
}

open class DynamicArrayList<T>(private val list: ArrayList<T>) : DynamicList<T>(list) {


    private val dynamicListContext = newSingleThreadContext("dynamic-list-context")
    //private val mutex = Mutex()
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): DynamicArrayList<T> = this

    override lateinit var sizeIn: Dynamic<Int>
    override val size by lazy {
        scope.launch {
            sizeIn = push(SizeId::class) {
                withContext(dynamicListContext) {
                    mutex.withLock {
                        list.size
                    }
                }
            }
        }
        while(!::sizeIn.isInitialized) {
            Thread.sleep(1)
        }
        sizeIn
    }
    override lateinit var isEmptyIn: Dynamic<Boolean>
    override val isEmpty by lazy {
        scope.launch {
            isEmptyIn = push(IsEmpty::class) {
                withContext(dynamicListContext){
                    mutex.withLock {
                        list.isEmpty()
                    }
                }
            }
        }
        while(!::isEmptyIn.isInitialized) {
            Thread.sleep(1)
        }
        isEmptyIn
    }


    suspend fun add(item: T): Boolean = coroutineScope{
        withContext(dynamicListContext){

            mutex.withLock {
                list.add(item)
                value = list
                true
            }
        }
    }
    suspend fun add(index: Int, item: T): Boolean = coroutineScope{
        withContext(dynamicListContext){
            mutex.withLock {
                list.add(index, item)
                value = list
                true
            }
        }
    }
    suspend fun removeFirst(){
        withContext(dynamicListContext){
            mutex.withLock {
                if(list.isNotEmpty()){
                    list.removeAt(0)
                    value = list
                }
            }
        }
    }
    suspend fun pop(): T  = coroutineScope{
        withContext(dynamicListContext) {
            mutex.withLock {

                with(list.first()) {
                    list.removeAt(0)
                    value = list//value.drop(1)
                    this
                }
            }
        }
    }
    // todo("Implement array-list and list-methods)

    inline fun <reified S> map(noinline f: (T)->S): DynamicArrayList<S> =
        DynamicArrayList(arrayListOf(*value.map(f).toTypedArray()))

}

suspend fun <S,T> DynamicArrayList<S>.onNext(action: suspend CoroutineScope.(S)->T): T = with( scope ) {
    blockWhileEmpty()
    //val oldSize = size.value
    //println("list-size = $oldSize")
    with(pop()!!) {
        //  blockUntil(size){it < oldSize}
        action(this)
    }

}


