package ir.farhangi.feature.auth.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PhoneRoute : NavKey

@Serializable
data class OtpRoute(val phone: String) : NavKey