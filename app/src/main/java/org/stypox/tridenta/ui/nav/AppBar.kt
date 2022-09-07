package org.stypox.tridenta.ui.nav

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.stypox.tridenta.R

@Composable
fun AppBarTitle(text: String) {
    Text(
        text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AppBarDrawerIcon(onDrawerClick: () -> Unit) {
    IconButton(onClick = onDrawerClick) {
        Icon(
            imageVector = Icons.Filled.Menu,
            contentDescription = stringResource(R.string.navigation_drawer)
        )
    }
}

@Composable
fun AppBarBackIcon(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = LocalTextStyle.current
    val colors = TextFieldDefaults.textFieldColors()

    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        MaterialTheme.colorScheme.onSurface
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor, lineHeight = 50.sp))

    // request focus and set the correct cursor position when this composable is first initialized
    val focusRequester = FocusRequester()
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    SideEffect {
        focusRequester.requestFocus()
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides LocalTextSelectionColors.current
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                // remove newlines to avoid strange layout issues, and also because singleLine=true
                val text = it.text.replace("\n", "")
                textFieldValue = it.copy(text = text)
                onValueChange(text)
            },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(32.dp)
                .indicatorLine(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors
                )
                .focusRequester(focusRequester),
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = true,
            decorationBox = { innerTextField ->
                // places text field with placeholder and appropriate bottom padding
                TextFieldDefaults.TextFieldDecorationBox(
                    value = value,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = innerTextField,
                    placeholder = { Text(text = hint) },
                    singleLine = true,
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(bottom = 4.dp)
                )
            }
        )
    }
}