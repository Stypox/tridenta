package org.stypox.tridenta.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit

/**
 * Displays a headline text by passing all of the provided parameters to [Text] and also passing
 * [Typography.headlineMedium] as `style`. A headline should be used sparingly for important texts
 * or numerals.
 */
@Composable
fun HeadlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing,
        textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, onTextLayout,
        style = MaterialTheme.typography.headlineMedium
    )
}

/**
 * Displays a title text by passing all of the provided parameters to [Text] and also passing
 * [Typography.titleMedium] as `style`. A title should be used for short texts and for primary
 * pieces of information (e.g. the name of an item in a list).
 */
@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing,
        textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, onTextLayout,
        style = MaterialTheme.typography.titleMedium
    )
}

/**
 * Displays a body text by passing all of the provided parameters to [Text] and also passing
 * [Typography.bodyMedium] as `style`. A body should be used for long texts, for subtitles and for
 * secondary pieces of information (e.g. the description of an item in a list).
 */
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing,
        textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, onTextLayout,
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Displays a label text by passing all of the provided parameters to [Text] and also passing
 * [Typography.labelMedium] as `style`. A label should be used sparingly for small pieces of text,
 * to annotate imagery or to introduce a headline.
 */
@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    Text(
        text, modifier, color, fontSize, fontStyle, fontWeight, fontFamily, letterSpacing,
        textDecoration, textAlign, lineHeight, overflow, softWrap, maxLines, onTextLayout,
        style = MaterialTheme.typography.labelMedium
    )
}

@Preview
@Composable
fun TextPreview() {
    Column {
        HeadlineText("Head\nline")
        TitleText("Title\ntitle")
        BodyText("Body\nbody")
        LabelText("Label\nbody")
    }
}