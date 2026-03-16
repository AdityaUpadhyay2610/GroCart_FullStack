package com.grocart.first.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grocart.first.R
import com.grocart.first.data.InternetItem
import com.grocart.first.data.Order
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyOrdersScreen(groViewModel: GroViewModel) {
    val orders by groViewModel.orders.collectAsState()

    if (orders.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Your Past Orders",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(orders.reversed()) { order ->
                OrderCard(order = order)
            }
        }
    } else {
        // UI for when there are no orders
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_box),
                contentDescription = "No Orders",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "No Orders Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Items you purchase will appear here.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// ✅ UPDATED: OrderCard now shows total price and handles aggregated items
@Composable
fun OrderCard(order: Order) {
    // Group items within the order to get quantities
    val itemsWithQuantity = order.items
        .groupBy { it.itemName } // Group by name (or a unique ID if you have one)
        .map { (_, items) ->
            // For each group, create an object with the item and its count
            com.grocart.first.data.InternetItemWithQuantity(items.first().toInternetItem(), items.size)
        }

    // Calculate the total price of this specific order
    val orderTotal = order.items.sumOf { it.itemPrice }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- Order Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(order.timestamp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF008069)
                )
                // ✅ ADDED: Display the total price for this order
                Text(
                    text = "Total: Rs. $orderTotal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- List of items in the order ---
            itemsWithQuantity.forEach { itemWithQuantity ->
                OrderItemRow(
                    item = itemWithQuantity.internetItem,
                    quantity = itemWithQuantity.quantity
                )
            }
        }
    }
}

// ✅ UPDATED: OrderItemRow now accepts a quantity
@Composable
fun OrderItemRow(item: InternetItem, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.itemName,
            modifier = Modifier.size(50.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(text = item.itemName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            // ✅ ADDED: Show quantity if more than 1
            if (quantity > 1) {
                Text(
                    text = "Quantity: $quantity",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        // Price for a single item
        Text(text = "Rs. ${item.itemPrice}", fontWeight = FontWeight.Medium)
    }
}

// Helper function to format the timestamp
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, yyyy 'at' hh:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}
