package com.grocart.first.network

import com.grocart.first.data.InternetItem
import com.grocart.first.data.Order
//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.*

// ✅ SERIALIZABLE CLASSES FOR LOGIN AND REGISTER
@Serializable
data class LoginRequest(val username: String, val password: String)
@Serializable
data class UserResponse(val id: Long, val username: String, val email: String)
@Serializable
data class CartRequest(val userId: Long, val itemName: String, val itemPrice: Int, val imageUrl: String, val quantity: Int = 1)

// ✅ RETROFIT SETUP
private const val BASE_URL = "http://10.0.2.2:8080"
private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }


private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface FirstApiService {
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems(): List<InternetItem>

    // MySQL Auth
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<UserResponse>
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: UserSignupRequest): Response<String>

    // MySQL Cart & Orders
    @POST("api/cart/add/{userId}")
    suspend fun addCartItem(
        @Path("userId") userId: Long,
        @Body item: InternetItem
    ): Response<okhttp3.ResponseBody>

    @GET("api/cart/{userId}")
    suspend fun getUserCart(@Path("userId") userId: Long): Response<List<com.grocart.first.data.CartItemResponse>>

    @DELETE("api/cart/clear/{userId}")
    suspend fun clearUserCart(@Path("userId") userId: Long): Response<okhttp3.ResponseBody>

    @POST("api/orders/place/{userId}")
    suspend fun placeOrder(@Path("userId") userId: Long, @Body total: Int): Response<String>

    @GET("api/orders/user/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): List<Order>
}

object FirstApi {
    val retrofitService: FirstApiService by lazy { retrofit.create(FirstApiService::class.java) }
}