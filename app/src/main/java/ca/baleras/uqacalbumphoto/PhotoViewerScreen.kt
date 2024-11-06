import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun PhotoViewerScreen(photos: List<String>, currentIndex: Int, navController: NavController, imageLoader: ImageLoader) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val maxScale = 3f
    val minScale = 0.9f

    val animatedScale = animateFloatAsState(targetValue = scale)
    val animatedOffsetX = animateFloatAsState(targetValue = offsetX)
    val animatedOffsetY = animateFloatAsState(targetValue = offsetY)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
                translationX = animatedOffsetX.value
                translationY = animatedOffsetY.value
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                    if (scale > 1f) {
                        val maxOffset = ((scale - 1) * size.width) / 2
                        offsetX = (offsetX + pan.x).coerceIn(-maxOffset, maxOffset)
                        offsetY = (offsetY + pan.y).coerceIn(-maxOffset, maxOffset)
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }

                    if (scale <= minScale) {
                        navController.navigate("photoGrid")
                    }
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (scale == 1f) {
                        val newIndex = if (dragAmount > 0) {
                            (currentIndex - 1).coerceAtLeast(0)
                        } else {
                            (currentIndex + 1).coerceAtMost(photos.size - 1)
                        }
                        navController.navigate("photoViewer/$newIndex")
                    }
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (scale == 1f) {
                        navController.navigate("photoGrid")
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale == 1f) maxScale else 1f
                    }
                )
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photos[currentIndex])
                .crossfade(true)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = null,
            imageLoader = imageLoader,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            }
        )
    }
}