package org.stypox.tridenta.ui.nav

import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
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
fun AppBarOpenIcon(onShareClick: () -> Unit) {
    IconButton(onClick = onShareClick) {
        Icon(
            imageVector = Icons.Filled.OpenInBrowser,
            contentDescription = stringResource(R.string.open)
        )
    }
}

@Composable
fun AppBarFavoriteIcon(isFavorite: Boolean, onFavoriteClicked: () -> Unit) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            if (!isFavorite) {
                // about to turn favorite: show an informative toast about launcher shortcuts
                Toast.makeText(context, R.string.add_shortcut_info, LENGTH_LONG).show()
            }
            onFavoriteClicked()
        }
    ) {
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
    // make sure there is no background color in the decoration box
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Unspecified,
        unfocusedContainerColor = Color.Unspecified,
        disabledContainerColor = Color.Unspecified,
    )

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
    var textFieldValueState by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }
    val textFieldValue = textFieldValueState.copy(text = value) // keep the value updated

    // copied from the BasicTextField implementation that takes a String
    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition) {
            textFieldValueState = textFieldValue
        }
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides LocalTextSelectionColors.current
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValueState = it
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
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = false,
                    placeholder = { Text(text = hint) },
                    colors = colors,
                    contentPadding = PaddingValues(bottom = 4.dp),
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
    TopAppBar(
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
    fun cancelSearch() {
        // clear the search filter (will not work properly, but prevents the old value from
        // appearing for the one frame before the runnable posted below is called)
        setSearchString("")
        // then close the search bar
        onSearchDone()
        // wait for the search bar to lose focus and so send the last onValueChange event (with the
        // previous value, not with "", that's why we need to clear the search string again)
        Handler(Looper.getMainLooper()).post {
            // on the next frame clear the search filter (now it will work)
            setSearchString("")
        }
    }

    TopAppBar(
        title = {
            AppBarTextField(
                value = searchString,
                onValueChange = setSearchString,
                hint = hint,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchDone() }),
                modifier = Modifier.onKeyEvent {
                    if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                        onSearchDone()
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                },
            )
        },
        navigationIcon = { AppBarBackIcon { cancelSearch() } },
        actions = {
            IconButton(onClick = { setSearchString("") }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.clear)
                )
            }
        }
    )

    BackHandler {
        // this will only be triggered if the user presses back when the keyboard is already closed
        cancelSearch()
    }
}
