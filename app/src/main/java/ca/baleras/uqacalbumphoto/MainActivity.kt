package ca.baleras.uqacalbumphoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.baleras.uqacalbumphoto.ui.theme.UqacAlbumPhotoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UqacAlbumPhotoTheme {
                val navController = rememberNavController()
                val devicePhotos = getDevicePhotos(this)
                val photos = if (devicePhotos.isNotEmpty()) {
                    devicePhotos
                } else {
                    List(200) { "https://picsum.photos/200/300?random=$it" }
                }
                NavHost(navController = navController, startDestination = "photoGrid") {
                    composable("photoGrid") {
                        PhotoGridScreen(navController, photos)
                    }
                    composable("photoViewer/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
                        PhotoViewerScreen(photos, index, navController)
                    }
                }
            }
        }
    }
}