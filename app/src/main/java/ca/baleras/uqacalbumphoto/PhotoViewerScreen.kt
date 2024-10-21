package ca.baleras.uqacalbumphoto

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter

@Composable
fun PhotoViewerScreen(photos: List<String>, currentIndex: Int, navController: NavController) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val maxScale = 3f
    val minScale = 0.9f
    var shouldNavigateBack by remember { mutableStateOf(false) }

    if (shouldNavigateBack) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                    if (scale > minScale) {
                        offsetX = (offsetX + pan.x).coerceIn(-scale * 1000f, scale * 1000f)
                        offsetY = (offsetY + pan.y).coerceIn(-scale * 1000f, scale * 1000f)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                    if (scale <= minScale) {
                        shouldNavigateBack = true
                    }
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (scale == 1f) {
                        val newIndex = when {
                            dragAmount > 0 && currentIndex > 0 -> currentIndex - 1
                            dragAmount < 0 && currentIndex < photos.size - 1 -> currentIndex + 1
                            else -> currentIndex
                        }
                        if (newIndex != currentIndex) {
                            navController.navigate("photoViewer/$newIndex") {
                                popUpTo("photoViewer/$currentIndex") { inclusive = true }
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (scale == 1f) {
                        shouldNavigateBack = true
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale == 1f) maxScale/2 else 1f
                    }
                )
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(photos[currentIndex]),
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