package com.grocart.first.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.google.firebase.auth.FirebaseAuth
import com.grocart.first.data.InternetItem
import com.grocart.first.data.Order
import com.grocart.first.data.SessionManager
import com.grocart.first.network.FirstApi
import com.grocart.first.network.LoginRequest
import com.grocart.first.network.UserResponse
import com.grocart.first.network.UserSignupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroViewModel(private val sessionManager: SessionManager) : ViewModel() {
    // ✅ State variables for the UI
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user: StateFlow<UserResponse?> = _user.asStateFlow()
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val _allItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val allItems: StateFlow<List<InternetItem>> = _allItems.asStateFlow()

    private val _cartItems = MutableStateFlow<List<com.grocart.first.data.CartItemResponse>>(emptyList())
    val cartItems: StateFlow<List<com.grocart.first.data.CartItemResponse>> = _cartItems.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isGuestSession = MutableStateFlow(false)
    val isGuestSession: StateFlow<Boolean> = _isGuestSession.asStateFlow()

    private val _logoutClicked = MutableStateFlow(false)
    val logoutClicked: StateFlow<Boolean> = _logoutClicked.asStateFlow()

    private val _showPaymentScreen = MutableStateFlow(false)
    val showPaymentScreen: StateFlow<Boolean> = _showPaymentScreen.asStateFlow()

    private val _paymentCountdown = MutableStateFlow(10)
    val paymentCountdown: StateFlow<Int> = _paymentCountdown.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _uiState = MutableStateFlow(GroUiState())
    val uiState: StateFlow<GroUiState> = _uiState.asStateFlow()

    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set
    private val _animatingItem = MutableStateFlow<InternetItem?>(null)
    val animatingItem = _animatingItem.asStateFlow()

    sealed interface ItemUiState {
        data class Success(val items: List<InternetItem>) : ItemUiState
        object Error : ItemUiState
        object Loading : ItemUiState
    }

    // Staggered init to stop the Davey lag
    init {
        checkExistingSession()
        viewModelScope.launch {
            delay(1000) // Allow UI to finish first frame
            launch { loadUserCart() }
            launch { getFirstItem() }
        }
    }

    //Function for Session checking
    private fun checkExistingSession() {
        val savedId = sessionManager.getUserId()
        val savedName = sessionManager.getUsername()
        if (savedId != -1L && savedName != null) {
            _user.value = UserResponse(id = savedId, username = savedName, email = "")
            _isGuestSession.value = false
        }

    }
    //function for trigering the animation in Item Screen When item is added to Cart.
    fun triggerAddToCartAnimation(item: InternetItem) {
        _animatingItem.value = item
        viewModelScope.launch {
            delay(800)
            _animatingItem.value = null
        }
    }



    // Logic to get filtered results
    fun getFilteredItems(query: String): List<InternetItem> {
        return if (query.trim().isEmpty()) {
            _allItems.value // Return everything if not searching
        } else {
            _allItems.value.filter {
                it.itemName.contains(query, ignoreCase = true) ||
                        it.itemCategory.contains(query, ignoreCase = true)
            }
        }
    }

    //function to Display Items in Cart and sync to MySQl database
    fun loadUserCart() {
        val userId = sessionManager.getUserId()
        if (userId == -1L) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FirstApi.retrofitService.getUserCart(userId)
                if (response.isSuccessful) {
                    val items = response.body() ?: emptyList()
                    _cartItems.update { items } // Safe update
                    Log.d("GROCART_DEBUG", "Cart Synced from MySQL: ${items.size}")
                }
            } catch (e: Exception) {
                Log.e("GROCART_DEBUG", "Cart sync failed: ${e.message}")
            }
        }
    }
    //function to place order and sync to MySQL
    fun placeOrder(total: Int) {
        val userId = sessionManager.getUserId()

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = FirstApi.retrofitService.placeOrder(userId, total)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Log.d("GROCART", "MySQL: Order placed and cart cleared!") //
                        _cartItems.value = emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e("GROCART", "MySQL Update Failed: ${e.message}")
            }
        }
    }

    //Function to add item in cart
    fun addToCart(item: InternetItem) {
        val userId = _user.value?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = FirstApi.retrofitService.addCartItem(userId, item)
                if (response.isSuccessful) {
                    loadUserCart() // Reload cart from server to get correct counts and ids
                }
            } catch (e: Exception) {
                Log.e("GROCART_DEBUG", "Add Cart error: ${e.message}")
            }
        }
    }

    //function to get items by search in items screen
    fun getFirstItem() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { itemUiState = ItemUiState.Loading }
            try {
                val items = FirstApi.retrofitService.getItems()
                withContext(Dispatchers.Main) {
                    itemUiState = ItemUiState.Success(items)
                    _allItems.value = items // 🔥 CRITICAL: Store data for the search logic
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { itemUiState = ItemUiState.Error }
            }
        }
    }

    // Helper functions
    fun setLogoutClicked(v: Boolean) { _logoutClicked.value = v }
    fun endGuestSession() { _isGuestSession.value = false }
    fun startGuestSession() { _isGuestSession.value = true }
    fun proceedToPay() { _showPaymentScreen.value = true }
    fun cancelPayment() { _showPaymentScreen.value = false }
    fun setPaymentCountdown(v: Int) { _paymentCountdown.value = v }
    fun updateSelectedCategory(cat: Int) { _uiState.update { it.copy(selectedCategory = cat) } }
    fun updateClickText(t: String) { _uiState.update { it.copy(clickStatus = t) } }

    fun loadAllProducts(products: List<InternetItem>) {
        _allItems.value = products
    }
    //Function for Login and Register
    fun login(u: String, p: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = FirstApi.retrofitService.loginUser(LoginRequest(u, p))
                if (res.isSuccessful) {
                    res.body()?.let {
                        _user.value = it
                        sessionManager.saveUserSession(it.id, it.username)
                        loadUserCart()
                    }
                }
            } catch (e: Exception) { Log.e("LOGIN", e.message ?: "") }
            finally { _loading.value = false }
        }
    }

    //Function for Logout and clearing data when logout is clicked
    fun logout() {
        sessionManager.logout()
        _user.value = null
        _cartItems.value = emptyList()
        _isGuestSession.value = false
        _logoutClicked.value = false
    }

    // Function to clear all data, including logout
    fun clearData() { logout() }

    // Function to register a new user and log them in
    fun register(u: String, e: String, p: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = FirstApi.retrofitService.registerUser(UserSignupRequest(u, e, p))
                if (res.isSuccessful) login(u, p)
            } catch (err: Exception) { Log.e("REG", err.message ?: "") }
            finally { _loading.value = false }
        }
    }

    // Function to show a fake payment complete animation
    fun completePayment() {
        if (_cartItems.value.isNotEmpty()) {
            val order = Order(items = _cartItems.value, timestamp = System.currentTimeMillis())
            _orders.update { it + order }
            _cartItems.value = emptyList()
        }
        _showPaymentScreen.value = false
    }

    // Function to removed the item from cart when payment animation will end
    // and update the cart and added to order screen
    // Function to remove the item from cart and update counts locally or refresh
    fun decreaseItemCount(item: com.grocart.first.data.CartItemResponse) {
        _cartItems.update { current ->
            val list = current.toMutableList()
            val idx = list.indexOfFirst { it.itemName == item.itemName }
            if (idx != -1) {
                val existing = list[idx]
                if (existing.quantity > 1) {
                    list[idx] = existing.copy(quantity = existing.quantity - 1)
                } else {
                    list.removeAt(idx)
                }
            }
            list
        }
    }
}