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
@Serializable
data class LoginRequest(val username: String, val password: String)
@Serializable
data class UserResponse(val id: Long, val username: String, val email: String)
@Serializable
data class CartRequest(val userId: Long, val itemName: String, val itemPrice: Int, val imageUrl: String, val quantity: Int = 1)

// TODO: Replace with your Railway URL after deploying (e.g. "https://grocart-backend.up.railway.app")
private const val BASE_URL = "https://YOUR-APP-NAME.up.railway.app"
private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }


private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()


interface FirstApiService {
    /**
     * Fetches the list of all grocery items.
     * @return List of InternetItem
     */
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems(): List<InternetItem>

    /**
     * Authenticates a user login request.
     * @param request The login credentials.
     * @return Response containing user data.
     */
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<UserResponse>

    /**
     * Registers a new user.
     * @param request The signup data.
     * @return Response string.
     */
    @POST("api/auth/register")
    suspend fun registerUser(@Body request: UserSignupRequest): Response<String>

    /**
     * Adds an item to the specified user's cart.
     * @param userId The ID of the user.
     * @param item The item to add.
     * @return Response body on success.
     */
    @POST("api/cart/add/{userId}")
    suspend fun addCartItem(
        @Path("userId") userId: Long,
        @Body item: InternetItem
    ): Response<okhttp3.ResponseBody>

    /**
     * Decreases the quantity of an item in the specified user's cart.
     * @param userId The ID of the user.
     * @param item The item to decrease.
     * @return Response body on success.
     */
    @POST("api/cart/decrease/{userId}")
    suspend fun decreaseCartItem(
        @Path("userId") userId: Long,
        @Body item: InternetItem
    ): Response<okhttp3.ResponseBody>

    /**
     * Retrieves the cart for a user.
     * @param userId The user's ID.
     * @return Response tracking the cart item list.
     */
    @GET("api/cart/{userId}")
    suspend fun getUserCart(@Path("userId") userId: Long): Response<List<com.grocart.first.data.CartItemResponse>>

    /**
     * Clears the cart for a user.
     * @param userId The user's ID.
     */
    @DELETE("api/cart/clear/{userId}")
    suspend fun clearUserCart(@Path("userId") userId: Long): Response<okhttp3.ResponseBody>

    /**
     * Places a new order for a user.
     * @param userId The user's ID.
     * @param total The order total.
     */
    @POST("api/orders/place/{userId}")
    suspend fun placeOrder(@Path("userId") userId: Long, @Body total: Int): Response<String>

    /**
     * Fetches past orders for a user.
     * @param userId The user's ID.
     * @return List of past Orders.
     */
    @GET("api/orders/user/{userId}")
    suspend fun getOrders(@Path("userId") userId: Long): List<Order>
}

object FirstApi {
    val retrofitService: FirstApiService by lazy { retrofit.create(FirstApiService::class.java) }
}