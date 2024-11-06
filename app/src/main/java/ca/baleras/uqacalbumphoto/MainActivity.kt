package ca.baleras.uqacalbumphoto

import PhotoViewerScreen
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.baleras.uqacalbumphoto.ui.theme.UqacAlbumPhotoTheme
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okio.Path.Companion.toOkioPath

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val imageLoader = ImageLoader.Builder(this)
            .memoryCache(MemoryCache.Builder(this).maxSizePercent(0.25).build())
            .diskCache(DiskCache.Builder().directory(filesDir.toOkioPath()).build())
            .build()

        setContent {
            UqacAlbumPhotoTheme {
                val navController = rememberNavController()
                val photos = loadPhotos()
                NavHost(navController = navController, startDestination = "photoGrid") {
                    composable("photoGrid") {
                        PhotoGridScreen(navController, photos, imageLoader)
                    }
                    composable("photoViewer/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
                        PhotoViewerScreen(photos, index, navController, imageLoader)
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        return ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadPhotos(): List<String> {
        if (!allPermissionsGranted()) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        return getDevicePhotos(this)
    }
}