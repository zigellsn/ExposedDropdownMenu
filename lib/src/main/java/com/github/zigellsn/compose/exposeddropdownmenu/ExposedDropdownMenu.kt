/*
 * Copyright 2021 Simon Zigelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.zigellsn.compose.exposeddropdownmenu

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.flow.collect

internal enum class TextFieldType {
    Filled, Outlined
}

/**
 * Material Design implementation of a Filled ExposedDropdownMenu
 * If you are looking for an outlined version, see OutlinedExposedDropdownMenu.
 * @param modifier a [Modifier] for this ExposesDropdownMenu
 * @param enabled controls the enabled state of the [ExposedDropdownMenu]. When `false`,
 * the exposed dropdown menu will be neither editable nor focusable, the input of the
 * text field will not be selectable, visually text field will appear in the disabled UI state
 * @param items the items to be shown in a list
 * @param label the optional label to be displayed inside the exposed dropdown menu container. The default
 * text style for internal [Text] is [Typography.caption] when the text field is in focus and
 * [Typography.subtitle1] when the text field is not in focus
 * @param defaultItem the default item to be selected. If the string is empty, no item will be selected
 * and the label will be shown
 * @param transformItemToString the transformation rule from the item type T to string
 * @param filter controls the regular expression which filters the items when a value is typed into
 * the text field
 * @param selectedItem gets called when an item is selected. It returns the item and the string
 * representation according to transformItemToString
 * @param content defines the appearance of an item in the list
 */
@Composable
fun <T> ExposedDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    items: List<T> = emptyList(),
    label: @Composable (() -> Unit)? = null,
    defaultItem: String = "",
    transformItemToString: (T) -> String = { it.toString() },
    filter: ((String) -> Regex)? = null,
    selectedItem: (Int, T) -> Unit = { _: Int, _: T -> },
    content: @Composable (String, T) -> Unit
) {
    ExposedDropdownMenuImpl(
        TextFieldType.Filled,
        defaultItem,
        items,
        enabled,
        transformItemToString,
        modifier,
        label,
        filter,
        selectedItem,
        content
    )
}

/**
 * Material Design implementation of a Filled ExposedDropdownMenu
 * @param modifier a [Modifier] for this ExposesDropdownMenu
 * @param enabled controls the enabled state of the [ExposedDropdownMenu]. When `false`,
 * the exposed dropdown menu will be neither editable nor focusable, the input of the
 * text field will not be selectable, visually text field will appear in the disabled UI state
 * @param items the items to be shown in a list
 * @param label the optional label to be displayed inside the exposed dropdown menu container. The default
 * text style for internal [Text] is [Typography.caption] when the text field is in focus and
 * [Typography.subtitle1] when the text field is not in focus
 * @param defaultItem the default item to be selected. If the string is empty, no item will be selected
 * and the label will be shown
 * @param transformItemToString the transformation rule from the item type T to string
 * @param filter controls the regular expression which filters the items when a value is typed into
 * the text field
 * @param selectedItem gets called when an item is selected. It returns the item and the string
 * representation according to transformItemToString
 * @param content defines the appearance of an item in the list
 */
@Composable
fun <T> OutlinedExposedDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    items: List<T> = emptyList(),
    label: @Composable (() -> Unit)? = null,
    defaultItem: String = "",
    transformItemToString: (T) -> String = { it.toString() },
    filter: ((String) -> Regex)? = null,
    selectedItem: (Int, T) -> Unit = { _: Int, _: T -> },
    content: @Composable (String, T) -> Unit
) {
    ExposedDropdownMenuImpl(
        TextFieldType.Outlined,
        defaultItem,
        items,
        enabled,
        transformItemToString,
        modifier,
        label,
        filter,
        selectedItem,
        content
    )
}

@Composable
internal fun <T> ExposedDropdownMenuImpl(
    type: TextFieldType,
    defaultItem: String,
    items: List<T>,
    enabled: Boolean = true,
    transformItemToString: (T) -> String,
    modifier: Modifier,
    label: @Composable (() -> Unit)? = null,
    filter: ((String) -> Regex)? = null,
    selectedItem: (Int, T) -> Unit,
    content: @Composable (String, T) -> Unit
) {
    var text by remember { mutableStateOf(defaultItem) }
    var expanded by remember { mutableStateOf(false) }

    val prepareFilterRegex: (String) -> Regex = filter
        ?: { filterText ->
            Regex(if (filterText == "")
                "^.*$"
            else {
                val sb = StringBuilder()
                sb.append("(?i)^.*")
                filterText.forEach {
                    sb.append(it)
                    sb.append(".*")
                }
                sb.append("$")
                sb.toString()
            })
        }

    val filteredItems = if (text.isNotEmpty()) {
        if (items.firstOrNull { transformItemToString(it) == text } == null)
            items.filter {
                transformItemToString(it).contains(prepareFilterRegex(text))
            }
        else
            items
    } else {
        items
    }

    ExposedDropdownMenuLayout(
        type = type,
        modifier = modifier,
        enabled = enabled,
        text = text,
        onTextChanged = {
            text = it
        },
        expanded = expanded,
        label = label,
        onToggle = {
            expanded = !expanded
        },
        onDismissRequest = {
            expanded = false
        },
        items = filteredItems,
        onValueChange = { _, item -> text = transformItemToString(item) },
        selectedItem = selectedItem,
        transformItemToString = transformItemToString,
        content = content
    )
}

@Composable
internal fun <T> ExposedDropdownMenuLayout(
    type: TextFieldType,
    modifier: Modifier,
    enabled: Boolean = true,
    text: String,
    onTextChanged: (String) -> Unit,
    expanded: Boolean,
    label: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onToggle: () -> Unit,
    items: List<T>,
    onValueChange: (Int, T) -> Unit = { _: Int, _: T -> },
    selectedItem: (Int, T) -> Unit = { _: Int, _: T -> },
    transformItemToString: (T) -> String,
    content: @Composable (String, T) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Release -> {
                    interactions.add(interaction)
                }
            }
        }
    }

    when (interactions.lastOrNull()) {
        is PressInteraction.Release -> {
            onToggle()
            interactions.remove(interactions.lastOrNull())
        }
    }

    val focusRequester = FocusRequester()

    Box(modifier = modifier) {
        if (type == TextFieldType.Outlined)
            OutlinedTextField(
                value = text,
                modifier = modifier
                    .testTag("edit_o")
                    .onFocusEvent {
                        if (!it.isFocused)
                            onDismissRequest()
                    }
                    .focusRequester(focusRequester),
                enabled = enabled,
                onValueChange = onTextChanged,
                label = label,
                trailingIcon = {
                    IconToggleButton(
                        modifier = Modifier.testTag("toggle_o"),
                        checked = expanded,
                        onCheckedChange = {
                            onToggle()
                            if (!expanded)
                                focusRequester.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = if (expanded) ArrowDropUp else ArrowDropDown,
                            contentDescription = stringResource(id = R.string.dropdown),
                        )
                    }
                },
                interactionSource = interactionSource
            )
        else
            TextField(
                value = text,
                modifier = modifier
                    .testTag("edit")
                    .onFocusEvent {
                        if (!it.isFocused)
                            onDismissRequest()
                    }
                    .focusRequester(focusRequester),
                enabled = enabled,
                onValueChange = onTextChanged,
                label = label,
                trailingIcon = {
                    IconToggleButton(
                        modifier = Modifier.testTag("toggle"),
                        checked = expanded,
                        onCheckedChange = {
                            onToggle()
                            if (!expanded)
                                focusRequester.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = if (expanded) ArrowDropUp else ArrowDropDown,
                            contentDescription = stringResource(id = R.string.dropdown),
                        )
                    }
                },
                interactionSource = interactionSource
            )
        if (items.isNotEmpty())
            DropDownList(
                items = items,
                modifier = modifier,
                expanded = expanded,
                onDismissRequest = onDismissRequest,
                selectedItem = selectedItem,
                onValueChange = onValueChange,
                transformItemToString = transformItemToString,
                content = content
            )
    }
}

@Composable
internal fun <T> DropDownList(
    items: List<T>,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit,
    selectedItem: (Int, T) -> Unit = { _: Int, _: T -> },
    onValueChange: (Int, T) -> Unit = { _: Int, _: T -> },
    transformItemToString: (T) -> String = { it.toString() },
    content: @Composable (String, T) -> Unit
) {
    DropdownMenu(
        modifier = modifier.defaultMinSize(
            minWidth = TextFieldDefaults.MinWidth,
        ),
        expanded = expanded,
        onDismissRequest = {},
        properties = PopupProperties(focusable = false)
    ) {
        items.forEachIndexed { index, item ->
            DropdownMenuItem(
                modifier = modifier,
                onClick = {
                    onDismissRequest()
                    selectedItem(index, item)
                    onValueChange(index, item)
                }
            ) {
                content(transformItemToString(item), item)
            }
        }
    }
}

// Copied from androidx.compose.material.icons.filled.Icons.Filled.ArrowDropDown
val ArrowDropDown: ImageVector
    get() {
        if (_arrowDropDown != null) {
            return _arrowDropDown!!
        }
        _arrowDropDown = materialIcon(name = "Filled.ArrowDropDown") {
            materialPath {
                moveTo(7.0f, 10.0f)
                lineToRelative(5.0f, 5.0f)
                lineToRelative(5.0f, -5.0f)
                close()
            }
        }
        return _arrowDropDown!!
    }

private var _arrowDropDown: ImageVector? = null

// Copied from androidx.compose.material.icons.filled.Icons.Filled.ArrowDropUp
val ArrowDropUp: ImageVector
    get() {
        if (_arrowDropUp != null) {
            return _arrowDropUp!!
        }
        _arrowDropUp = materialIcon(name = "Filled.ArrowDropUp") {
            materialPath {
                moveTo(7.0f, 14.0f)
                lineToRelative(5.0f, -5.0f)
                lineToRelative(5.0f, 5.0f)
                close()
            }
        }
        return _arrowDropUp!!
    }

private var _arrowDropUp: ImageVector? = null