package com.grocart.first.ui

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.grocart.first.R
import com.grocart.first.data.InternetItem

@Composable
fun InternetItemScreen(
    groViewModel: GroViewModel,
    itemUiState: GroViewModel.ItemUiState
){
    when(itemUiState){
        is GroViewModel.ItemUiState.Loading -> LoadingScreen()
        is GroViewModel.ItemUiState.Success -> {
            ItemScreen(groViewModel = groViewModel, items = itemUiState.items)
        }

        else -> ErrorScreen(groViewModel = groViewModel)
    }
}

@Composable
fun ItemScreen(
    groViewModel: GroViewModel,
    items : List<InternetItem>
) {
    val groUiState by groViewModel.uiState.collectAsState()
    val selectedCategory = stringResource(groUiState.selectedCategory)

    val animatingItem by groViewModel.animatingItem.collectAsState()

    val database = remember(items, selectedCategory) {
        if (items.isEmpty()) emptyList()
        else items.filter { 
            val dbCat = it.itemCategory.trim()
            val uiCat = selectedCategory.trim()
            dbCat.equals(uiCat, ignoreCase = true) || 
            uiCat.contains(dbCat, ignoreCase = true) || 
            dbCat.contains(uiCat, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            item(span = { GridItemSpan(1) }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                ) {
                    Text(
                        text = "$selectedCategory (${database.size})",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            }

            items(database) { currentItem ->
                ItemCard(
                    itemName = currentItem.itemName,
                    imageUrl = currentItem.imageUrl,
                    quantityLabel = currentItem.itemQuantity,
                    itemPrice = currentItem.itemPrice,
                    itemCategory = currentItem.itemCategory,
                    groViewModel = groViewModel
                )
            }
        }

        animatingItem?.let { item ->
            FlyingItemAnimation(item)
        }
    }
}

@Composable
fun FlyingItemAnimation(item: InternetItem) {
    var isStarted by remember { mutableStateOf(false) }

    val animatedY by animateFloatAsState(
        targetValue = if (isStarted) 1000f else 0f,
        animationSpec = tween(700),
        label = "yAnimation"
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isStarted) 0f else 1f,
        animationSpec = tween(700),
        label = "alphaAnimation"
    )

    LaunchedEffect(Unit) { isStarted = true }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .offset(y = animatedY.dp)
                .alpha(animatedAlpha)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun ItemCard(
    itemName: String,
    imageUrl: String,
    quantityLabel: String,
    itemPrice: Int,
    itemCategory: String,
    groViewModel: GroViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.width(150.dp).padding(8.dp)) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = itemName,
                    modifier = Modifier.fillMaxWidth().height(110.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(244, 67, 54, 255)),
                        shape = RoundedCornerShape(bottomStart = 8.dp)
                    ) {
                        Text(text = "25% off", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }

        Text(text = itemName, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 5.dp), maxLines = 1)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "Rs. $itemPrice", fontSize = 12.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                Text(text = "Rs. ${itemPrice * 75 / 100}", fontSize = 20.sp, color = Color(255, 116, 105, 255))
            }
            Text(text = quantityLabel, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable {
                val currentItem = InternetItem(
                    itemName = itemName,
                    imageUrl = imageUrl,
                    itemQuantity = quantityLabel,
                    itemPrice = itemPrice,
                    itemCategory = itemCategory
                )
                groViewModel.triggerAddToCartAnimation(currentItem)
                groViewModel.addToCart(currentItem)

                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show()
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Add to Cart", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp))
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(groViewModel: GroViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.error), contentDescription = "Error")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { groViewModel.getFirstItem() }) {
            Text("Retry")
        }
    }
}