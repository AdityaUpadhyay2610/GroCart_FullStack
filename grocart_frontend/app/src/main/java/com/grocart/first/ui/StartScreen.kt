package com.grocart.first.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grocart.first.data.DataSource

@Composable
fun StartScreen(
    groViewModel: GroViewModel,
    onCategoryClicked: (Int) -> Unit
) {
    val context = LocalContext.current
    val allCategories = remember { DataSource.loadCategories() }

    Column(Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(215, 208, 208, 205)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Shop By Category",
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.Black,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            items(allCategories) { item ->
                CategoryCard(
                    context = context,
                    stringResourceId = item.stringResourceId,
                    imageResourceId = item.imageResourceId,
                    groViewModel = groViewModel,
                    onCategoryClicked = onCategoryClicked
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    context: Context,
    stringResourceId: Int,
    imageResourceId: Int,
    groViewModel: GroViewModel,
    onCategoryClicked: (Int) -> Unit
) {
    val categoryName = stringResource(id = stringResourceId)

    Card(
        onClick = {
            groViewModel.updateClickText(categoryName)
            Toast.makeText(context, "$categoryName selected", Toast.LENGTH_SHORT).show()
            onCategoryClicked(stringResourceId)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = categoryName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Image(
                painter = painterResource(imageResourceId),
                contentDescription = categoryName,
                modifier = Modifier.size(150.dp)
            )
        }
    }
}