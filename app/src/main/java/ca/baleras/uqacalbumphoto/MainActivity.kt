package ca.baleras.uqacalbumphoto

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.baleras.uqacalbumphoto.ui.theme.UqacAlbumPhotoTheme

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_MEDIA_IMAGES
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate: ")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("TAG", "onCreate: allPermissionsGranted")
        setContent {
            UqacAlbumPhotoTheme {
                val navController = rememberNavController()
                val photos = loadPhotos()
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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        Log.d("TAG", "allPermissionsGranted: ")
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadPhotos(): List<String> {
        Log.d("TAG", "loadPhotos: ")
        return getDevicePhotos(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setContent {
                    UqacAlbumPhotoTheme {
                        val navController = rememberNavController()
                        val photos = loadPhotos()
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
            } else {
                // Handle the case where permissions are not granted
                List(200) { "https://picsum.photos/200/300?random=$it" }
            }
        }
    }
}