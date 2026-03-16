package com.grocart.first.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.grocart.first.data.InternetItem
import com.grocart.first.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.grocart.first.data.CartItemResponse
import com.grocart.first.data.DataSource

/** Enum class to define available screens and their titles */
enum class GroAppScreen(val title: String) {
    Start("GroCart"),
    Item("Choose Items"),
    Cart("Your Cart"),
    Orders("My Orders")
}

// Global variable for back navigation
var canNavigateBack = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstApp(
    groViewModel: GroViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    // Search State for Predictive Search
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Collect states from MySQL-based ViewModel
    val user by groViewModel.user.collectAsState()
    val logoutClicked by groViewModel.logoutClicked.collectAsState()
    val cartItems by groViewModel.cartItems.collectAsState()
    val isGuest by groViewModel.isGuestSession.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = GroAppScreen.valueOf(
        backStackEntry?.destination?.route ?: GroAppScreen.Start.name
    )

    canNavigateBack = navController.previousBackStackEntry != null

    // Login logic updated to check MySQL user
    if (user == null && !isGuest) {
        LoginUi(groViewModel = groViewModel)
    } else {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentScreen.title,
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                                    )
                                    if (currentScreen == GroAppScreen.Cart) {
                                        Text(
                                            text = " (${cartItems.size})",
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Logout trigger
                                Row(modifier = Modifier.clickable { groViewModel.setLogoutClicked(true) }) {
                                    Icon(painter = painterResource(R.drawable.logout), contentDescription = "Logout", modifier = Modifier.size(24.dp))
                                    Text(text = "Logout", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                                }
                            }
                        },
                        navigationIcon = {
                            if (canNavigateBack) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        }
                    )

                    // --- BLINKIT STYLE SEARCH BAR ---
                    if (currentScreen == GroAppScreen.Start || currentScreen == GroAppScreen.Item) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Search 'Milk', 'Bread'...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                    }
                }
            },
            bottomBar = {
                FirstAppBar(navController = navController, currentScreen = currentScreen, cartItems = cartItems, groViewModel = groViewModel)
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavHost(navController = navController, startDestination = GroAppScreen.Start.name) {
                    composable(route = GroAppScreen.Start.name) {
                        StartScreen(groViewModel = groViewModel, onCategoryClicked = { cat ->
                            groViewModel.updateSelectedCategory(cat)
                            navController.navigate(GroAppScreen.Item.name)
                        })
                    }
                    composable(route = GroAppScreen.Item.name) {
                        InternetItemScreen(groViewModel = groViewModel, itemUiState = groViewModel.itemUiState)
                    }
                    composable(route = GroAppScreen.Cart.name) {
                        CartScreen(groViewModel = groViewModel, onHomeButtonClicked = {
                            navController.navigate(GroAppScreen.Start.name) { popUpTo(0) }
                        })
                    }
                    composable(route = GroAppScreen.Orders.name) {
                        MyOrdersScreen(groViewModel = groViewModel)
                    }
                }

                // --- PREDICTIVE SEARCH OVERLAY ---
                if (searchQuery.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        PredictiveResultList(
                            query = searchQuery,
                            groViewModel = groViewModel,
                            // Inside your PredictiveResultList onItemClick in FirstApp.kt
                            onItemClick = { item ->
                                searchQuery = "" // Reset search bar

                                // 1. Get the list of categories from your DataSource
                                val categoryList = DataSource.loadCategories()

                                // 2. Find the category where the name matches your item's category
                                // We use context.getString to compare the actual text names

                                val matchedCategory = categoryList.find { cat ->
                                    context.getString(cat.stringResourceId) == item.itemCategory
                                }

                                if (matchedCategory != null) {
                                    // Pass the valid Resource ID (e.g., 2131886123) instead of 0
                                    groViewModel.updateSelectedCategory(matchedCategory.stringResourceId)
                                    navController.navigate(GroAppScreen.Item.name)
                                } else {
                                    // Safety: If no match is found, don't navigate or use a default
                                    Log.e("GROCART_ERROR", "Category name ${item.itemCategory} not found in DataSource")
                                }
                            }
                        )
                    }
                }
            }

            if (logoutClicked) {
                AlertCheck(
                    onYesButtonPressed = {
                        groViewModel.setLogoutClicked(false)
                        groViewModel.clearData()
                    },
                    onNoButtonPressed = { groViewModel.setLogoutClicked(false) }
                )
            }
        }
    }
}

@Composable
fun PredictiveResultList(
    query: String,
    groViewModel: GroViewModel,
    onItemClick: (InternetItem) -> Unit
) {
    val filteredResults = groViewModel.getFilteredItems(query)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(filteredResults) { item ->
            ListItem(
                headlineContent = { Text(item.itemName, fontWeight = FontWeight.Bold) },
                supportingContent = { Text("Category: ${item.itemCategory}") },
                trailingContent = { Text("₹${item.itemPrice}", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold) },
                modifier = Modifier.clickable { onItemClick(item) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}

@Composable
fun FirstAppBar(
    navController: NavHostController,
    currentScreen: GroAppScreen,
    cartItems: List<CartItemResponse>,
    groViewModel: GroViewModel
) {
    val isGuest by groViewModel.isGuestSession.collectAsState()
    var showLoginPrompt by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, "Home") },
            label = { Text("Home") },
            selected = currentScreen == GroAppScreen.Start,
            onClick = { navController.navigate(GroAppScreen.Start.name) { popUpTo(0) } }
        )

        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Outlined.List, "Orders") },
            label = { Text("Orders") },
            selected = currentScreen == GroAppScreen.Orders,
            onClick = {
                if (isGuest) showLoginPrompt = true
                else navController.navigate(GroAppScreen.Orders.name) { popUpTo(0) }
            }
        )

        NavigationBarItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItems.isNotEmpty()) {
                            Badge(containerColor = Color.Red) {
                                Text(cartItems.size.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ShoppingCart, "Cart")
                }
            },
            label = { Text("Cart") },
            selected = currentScreen == GroAppScreen.Cart,
            onClick = { navController.navigate(GroAppScreen.Cart.name) }
        )
    }

    if (showLoginPrompt) {
        AlertDialog(
            onDismissRequest = { showLoginPrompt = false },
            title = { Text("Login Required") },
            text = { Text("Please login to see your orders.") },
            confirmButton = { TextButton(onClick = { groViewModel.endGuestSession(); showLoginPrompt = false }) { Text("Login") } },
            dismissButton = { TextButton(onClick = { showLoginPrompt = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AppNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Icon(icon, label)
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AlertCheck(onYesButtonPressed: () -> Unit, onNoButtonPressed: () -> Unit) {
    AlertDialog(
        onDismissRequest = onNoButtonPressed,
        title = { Text("Logout?", fontWeight = FontWeight.ExtraBold) },
        text = { Text("Are you sure you want to logout?") },
        confirmButton = { TextButton(onClick = onYesButtonPressed) { Text("Yes") } },
        dismissButton = { TextButton(onClick = onNoButtonPressed) { Text("No") } }
    )
}