package shared

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraManager(onResult: (SharedImage?) -> Unit): CameraManager


class CameraManager(
    private val onLaunch: () -> Unit
) {
    fun launch() = onLaunch()
}
