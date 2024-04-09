package shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIPasteboard

class iOSClipboardController : ClipboardController {

    private val clipboard = UIPasteboard.generalPasteboard

    override fun copy(text: String) {
        clipboard.string = text
    }

    override fun paste(): String? {
        return clipboard.string
    }
}

@Composable
actual fun rememberClipboardController(): ClipboardController {
    return remember { iOSClipboardController() }
}
