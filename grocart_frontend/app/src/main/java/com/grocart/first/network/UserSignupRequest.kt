package com.grocart.first.network////package com.grocart.first.network
////
import kotlinx.serialization.Serializable
// ✅ SERIALIZABLE CLASSES FOR LOGIN AND REGISTER
@Serializable
data class UserSignupRequest(
    val username: String,
    val email: String,
    val password: String
)