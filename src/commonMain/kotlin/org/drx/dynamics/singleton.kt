package org.drx.dynamics

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Singleton<T: Any > : ReadOnlyProperty<Any?, Singleton<T>> {
    /**
     * Returns the value of the property for the given object.
     * @param thisRef the object for which the value is requested.
     * @param property the metadata for the property.
     * @return the property value.
     */
    override fun getValue(thisRef: Any?, property: KProperty<*>): Singleton<T> = this

    private val prop: ReadOnlyProperty<Any?, T>
    private val notNull: ReadWriteProperty<Any? , T> = Delegates.notNull<T>()
    private var nonNullValue by notNull
    internal lateinit var isSet: Unit
    internal var isInitialized: Boolean = false

    init {
        prop = notNull
    }

    val value by prop
    
    fun put(value1: T) {
        if(!::isSet.isInitialized) {
            isSet = Unit
            nonNullValue = value1
            isInitialized = true
        }
    }
    
    suspend fun put(block: suspend CoroutineScope.()->T) = coroutineScope {
        with(block()) {
            put(this)
        }
    }
    suspend fun <S> onInit(f: suspend CoroutineScope.(T)->S): S = coroutineScope {
        while(!isInitialized) {
            delay(1)
        }
        f(value)
    }
}

fun <T>T.get(): T = this


