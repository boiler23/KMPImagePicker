package shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidClipboardController(private val context: Context) : ClipboardController {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun copy(text: String) {
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun paste(): String? {
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            return clip.getItemAt(0).coerceToText(context).toString()
        }
        return null
    }
}

@Composable
actual fun rememberClipboardController(): ClipboardController {
    val context = LocalContext.current
    return remember(context) { AndroidClipboardController(context) }
}
