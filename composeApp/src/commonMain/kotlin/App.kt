import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import shared.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        var imageSourceOptionDialog by remember { mutableStateOf(value = false) }
        var launchCamera by remember { mutableStateOf(value = false) }
        var launchGallery by remember { mutableStateOf(value = false) }
        var launchSetting by remember { mutableStateOf(value = false) }
        var permissionRationalDialog by remember { mutableStateOf(value = false) }
        var text by remember { mutableStateOf(TextFieldValue("")) }
        val clipboardController = rememberClipboardController()
        val permissionsManager = createPermissionsManager(object : PermissionCallback {
            override fun onPermissionStatus(
                permissionType: PermissionType,
                status: PermissionStatus
            ) {
                when (status) {
                    PermissionStatus.GRANTED -> {
                        when (permissionType) {
                            PermissionType.CAMERA -> launchCamera = true
                            PermissionType.GALLERY -> launchGallery = true
                        }
                    }

                    else -> {
                        permissionRationalDialog = true
                    }
                }
            }


        })

        val cameraManager = rememberCameraManager {
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.Default) {
                    it?.toImageBitmap()
                }
                imageBitmap = bitmap
            }
        }

        val galleryManager = rememberGalleryManager {
            coroutineScope.launch {
                val bitmap = withContext(Dispatchers.Default) {
                    it?.toImageBitmap()
                }
                imageBitmap = bitmap
            }
        }
        if (imageSourceOptionDialog) {
            ImageSourceOptionDialog(onDismissRequest = {
                imageSourceOptionDialog = false
            }, onGalleryRequest = {
                imageSourceOptionDialog = false
                launchGallery = true
            }, onCameraRequest = {
                imageSourceOptionDialog = false
                launchCamera = true
            })
        }
        if (launchGallery) {
            if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
                galleryManager.launch()
            } else {
                permissionsManager.askPermission(PermissionType.GALLERY)
            }
            launchGallery = false
        }
        if (launchCamera) {
            if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
                cameraManager.launch()
            } else {
                permissionsManager.askPermission(PermissionType.CAMERA)
            }
            launchCamera = false
        }
        if (launchSetting) {
            permissionsManager.launchSettings()
            launchSetting = false
        }
        if (permissionRationalDialog) {
            AlertMessageDialog(title = "Permission Required",
                message = "To set your profile picture, please grant this permission. You can manage permissions in your device settings.",
                positiveButtonText = "Settings",
                negativeButtonText = "Cancel",
                onPositiveClick = {
                    permissionRationalDialog = false
                    launchSetting = true

                },
                onNegativeClick = {
                    permissionRationalDialog = false
                })

        }
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.wrapContentHeight().background(Color.DarkGray),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Profile",
                        modifier = Modifier.size(100.dp).clip(CircleShape).clickable {
                            imageSourceOptionDialog = true
                                                                                     },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        modifier = Modifier.size(100.dp).clip(CircleShape).clickable {
                            imageSourceOptionDialog = true
                                                                                     },
                        painter = painterResource("ic_person_circle.xml"),
                        contentDescription = "Profile",
                        )
                }
                TextField(
                    text,
                    onValueChange = {
                        text = it
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { clipboardController.copy(text.text) }) {
                        Text("Copy")
                    }
                    Button(onClick = { clipboardController.paste()?.let { text = TextFieldValue(it) } }) {
                        Text(text = "Paste")
                    }
                }
            }
        }
    }
}

