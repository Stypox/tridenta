package org.stypox.tridenta.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.stypox.tridenta.R


@Preview(widthDp = 128, heightDp = 128)
@Composable
fun AppLauncherIcon(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.mipmap.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        Image(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            contentScale = object : ContentScale {
                override fun computeScaleFactor(
                    srcSize: Size,
                    dstSize: Size
                ): ScaleFactor {
                    return ScaleFactor(
                        dstSize.width / srcSize.width * 1.5f,
                        dstSize.height / srcSize.height * 1.5f
                    )
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}