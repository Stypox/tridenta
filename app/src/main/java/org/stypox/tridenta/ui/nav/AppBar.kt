package org.stypox.tridenta.ui.nav

import android.view.KeyEvent
import android.view.ViewTreeObserver
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.theme.AppTheme

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

@Composable
fun AppBarFavoriteIcon(isFavorite: Boolean, onFavoriteClicked: () -> Unit) {
    IconButton(onClick = onFavoriteClicked) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(R.string.favorite)
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

    // request focus when this composable is first initialized
    val focusRequester = FocusRequester()
    SideEffect {
        focusRequester.requestFocus()
    }

    // set the correct cursor position when this composable is first initialized
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    textFieldValue = textFieldValue.copy(text = value) // make sure to keep the value updated

    CompositionLocalProvider(
        LocalTextSelectionColors provides LocalTextSelectionColors.current
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                // remove newlines to avoid strange layout issues, and also because singleLine=true
                onValueChange(it.text.replace("\n", ""))
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


@Composable
fun SearchTopAppBar(
    searchString: String,
    setSearchString: (String) -> Unit,
    title: String,
    hint: String,
    navigationIcon: @Composable () -> Unit
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }

    if (searchExpanded) {
        SearchTopAppBarExpanded(
            searchString = searchString,
            setSearchString = setSearchString,
            hint = hint,
            onSearchDone = { searchExpanded = false }
        )
    } else {
        SearchTopAppBarUnexpanded(
            title = searchString.ifEmpty { title },
            onSearchClick = { searchExpanded = true },
            navigationIcon = navigationIcon
        )
    }
}

@Preview
@Composable
private fun SearchTopAppBarPreview() {
    var searchString by rememberSaveable { mutableStateOf("") }
    AppTheme {
        SearchTopAppBar(
            searchString = searchString,
            setSearchString = { searchString = it },
            title = "The title",
            hint = "The hint…",
            navigationIcon = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopAppBarUnexpanded(
    title: String,
    onSearchClick: () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            AppBarTitle(text = title)
        },
        navigationIcon = navigationIcon,
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopAppBarExpanded(
    searchString: String,
    setSearchString: (String) -> Unit,
    hint: String,
    onSearchDone: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            // close the search if the keyboard is closed
            val view = LocalView.current
            DisposableEffect(view) {
                var wasKeyboardOpen = false
                val listener = ViewTreeObserver.OnGlobalLayoutListener {
                    val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                        ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
                    if (wasKeyboardOpen && !isKeyboardOpen) {
                        onSearchDone()
                    }
                    wasKeyboardOpen = isKeyboardOpen
                }

                view.viewTreeObserver.addOnGlobalLayoutListener(listener)
                onDispose {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
                }
            }

            AppBarTextField(
                value = searchString,
                onValueChange = setSearchString,
                hint = hint,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearchDone() }
                ),
                modifier = Modifier.onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        onSearchDone()
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                },
            )
        },
        navigationIcon = {
            AppBarBackIcon(onBackClick = onSearchDone)
        },
        actions = {
            IconButton(onClick = { setSearchString("") }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.clear)
                )
            }
        }
    )
}
