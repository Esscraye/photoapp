package ca.baleras.uqacalbumphoto

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.baleras.uqacalbumphoto.ui.theme.UqacAlbumPhotoTheme
import coil3.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UqacAlbumPhotoTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "photoGrid") {
                    composable("photoGrid") {
                        val photos = List(200) { "https://via.placeholder.com/150" }
                        val onPhotoClick: (String) -> Unit = { photo ->
                            navController.navigate("photoViewer/${Uri.encode(photo)}")
                        }
                        PhotoGrid(photos = photos, onPhotoClick = onPhotoClick)
                    }
                    composable("photoViewer/{photo}") { backStackEntry ->
                        val photo = backStackEntry.arguments?.getString("photo")
                        photo?.let {
                            PhotoViewer(photo = Uri.decode(it), navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGrid(photos: List<String>, onPhotoClick: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = modifier
    ) {
        items(photos) { photo ->
            Image(
                painter = rememberAsyncImagePainter(photo),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onPhotoClick(photo) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PhotoViewer(photo: String, navController: NavController, modifier: Modifier = Modifier) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val maxScale = 3f
    val minScale = 1f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                    offsetX += pan.x
                    offsetY += pan.y
                    if (scale <= minScale) {
                        navController.popBackStack()
                    }
                }
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoGridPreview() {
    UqacAlbumPhotoTheme {
        PhotoGrid(photos = List(200) { "https://via.placeholder.com/150" }, onPhotoClick = {})
    }
}