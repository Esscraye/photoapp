package ca.baleras.uqacalbumphoto

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter

@Composable
fun PhotoGridScreen(navController: NavController, photos: List<String>, imageLoader: ImageLoader) {
    var zoomLevel by remember { mutableStateOf(1f) }

    // Adjust the number of columns based on the zoom level
    val columns = if (zoomLevel > 1f) 2 else 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    zoomLevel *= zoom
                    zoomLevel = zoomLevel.coerceIn(0.5f, 2f) // Limit the zoom level
                }
            }
    ) {
        items(photos.size) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
            ) {
                var isLoading by remember { mutableStateOf(true) }
                var isError by remember { mutableStateOf(false) }

                Image(
                    painter = rememberAsyncImagePainter(
                        model = photos[index],
                        imageLoader = imageLoader,
                        onLoading = { isLoading = true },
                        onError = { isLoading = false; isError = true },
                        onSuccess = { isLoading = false }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { navController.navigate("photoViewer/$index") },
                    contentScale = ContentScale.Crop
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (isError) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}