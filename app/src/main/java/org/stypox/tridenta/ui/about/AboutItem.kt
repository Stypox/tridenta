package org.stypox.tridenta.ui.about

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.ui.theme.TitleText
import java.util.*

@Composable
fun AboutItem(
    icon: @Composable (Modifier) -> Unit,
    clipIcon: Boolean,
    title: String,
    description: String,
    buttonTextAndUri: Pair<String, String>?,
) {
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            icon(
                Modifier
                    .padding(16.dp)
                    .size(88.dp)
                    .run {
                        if (clipIcon) {
                            clip(MaterialTheme.shapes.extraLarge)
                        } else {
                            this
                        }
                    }
            )

            Column(
                modifier = if (buttonTextAndUri == null) {
                    Modifier
                        .padding(start = 0.dp, top = 20.dp, end = 16.dp, bottom = 20.dp)
                        .fillMaxWidth()
                } else {
                    Modifier
                        .padding(start = 0.dp, top = 20.dp, end = 16.dp, bottom = 4.dp)
                        .fillMaxSize()
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleText(
                    text = title,
                    modifier = Modifier.padding(bottom = 2.dp),
                    textAlign = TextAlign.Center
                )

                BodyText(
                    text = description,
                    textAlign = TextAlign.Center
                )

                if (buttonTextAndUri != null) {
                    Spacer(modifier = Modifier.weight(1.0f))

                    val (buttonText, buttonUri) = buttonTextAndUri
                    val uriHandler = LocalUriHandler.current

                    TextButton(
                        onClick = { uriHandler.openUri(buttonUri) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        LabelText(text = buttonText.uppercase(Locale.getDefault()))
                    }
                }
            }
        }
    }
}

@Composable
fun AboutItem(
    icon: ImageVector,
    clipIcon: Boolean,
    title: String,
    description: String,
    buttonTextAndUri: Pair<String, String>?,
) {
    AboutItem(
        icon = { modifier ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier
            )
        },
        clipIcon = clipIcon,
        title = title,
        description = description,
        buttonTextAndUri = buttonTextAndUri
    )
}

@Composable
fun AboutItem(
    @DrawableRes icon: Int,
    clipIcon: Boolean,
    title: String,
    description: String,
    buttonTextAndUri: Pair<String, String>?,
) {
    AboutItem(
        icon = { modifier ->
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = modifier
            )
        },
        clipIcon = clipIcon,
        title = title,
        description = description,
        buttonTextAndUri = buttonTextAndUri
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun AboutItemListPreview() {
    Column {
        AboutItem(
            icon = Icons.Filled.LocationCity,
            clipIcon = false,
            title = stringResource(R.string.app_name),
            description = "An app to view buses",
            buttonTextAndUri = Pair("Click here", "")
        )
        AboutItem(
            icon = R.drawable.stypox,
            clipIcon = true,
            title = stringResource(R.string.app_name),
            description = "An app to view buses",
            buttonTextAndUri = null
        )
        AboutItem(
            icon = { modifier -> AppLauncherIcon(modifier) },
            clipIcon = true,
            title = "Long title ".repeat(5),
            description = "An app to view buses ".repeat(7),
            buttonTextAndUri = Pair("C l i c k h e r e ".repeat(10), "")
        )
    }
}