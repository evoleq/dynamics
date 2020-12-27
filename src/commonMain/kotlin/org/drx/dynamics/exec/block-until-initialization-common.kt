package org.drx.dynamics.exec

import kotlinx.coroutines.*
import org.drx.dynamics.Singleton
import org.drx.dynamics.markers.EvoleqDsl

@EvoleqDsl
suspend fun <T : Any> blockUntilInitializationOf(singleton: Singleton<T>): Unit =
    if(!singleton.isInitialized) {
        try {
            val eternity: Deferred<Unit> = CoroutineScope(Job()).async {
                delay(Long.MAX_VALUE)
            }
            singleton.onInit {
                eternity.cancel()
            }
            eternity.await()
        } catch (ignored: Exception) { }
    }  else { Unit }


@EvoleqDsl
suspend fun <T : Any> Singleton<T>.block() = blockUntilInitializationOf(this)