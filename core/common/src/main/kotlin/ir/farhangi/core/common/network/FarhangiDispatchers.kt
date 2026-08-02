package ir.farhangi.core.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val farhangiDispatcher: FarhangiDispatchers)

enum class FarhangiDispatchers {
    Default,
    Io,
    Main,
}