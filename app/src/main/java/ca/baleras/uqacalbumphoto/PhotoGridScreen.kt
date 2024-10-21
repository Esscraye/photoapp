package ca.baleras.uqacalbumphoto

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter

@Composable
fun PhotoGridScreen(navController: NavController, photos: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        items(photos.size) { index ->
            val photo = photos[index]
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { navController.navigate("photoViewer/$index") },
                contentScale = ContentScale.Crop
            )
        }
    }
}