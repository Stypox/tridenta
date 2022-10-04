package org.stypox.tridenta.util

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView

class ShortcutBitmapGeneratorView @JvmOverloads constructor(
    context: Context,
    val content: (@Composable () -> Unit)? = null,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    @Composable
    override fun Content() {
        // This is a ComposableUI function
        content?.invoke()
    }
}