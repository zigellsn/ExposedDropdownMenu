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

import androidx.activity.ComponentActivity
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.FlowPreview
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@RunWith(AndroidJUnit4::class)
class ExposedDropdownMenuTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            MaterialTheme {
                Button(onClick = { }) {
                    Text(text = "Focus")
                }
                ExposedDropdownMenu(
                    items = listOf("Abb", "Abc", "A", "B", "C"),
                    label = { Text(text = "Test") }
                ) { text, _ ->
                    Text(text = text)
                }
                OutlinedExposedDropdownMenu(
                    items = listOf("Abb", "Abc", "A", "B", "C"),
                    label = { Text(text = "Test") }
                ) { text, _ ->
                    Text(text = text)
                }
            }
        }
    }

    @Test
    fun exposedDropdownMenuExpandTest() {
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onNodeWithTag("toggle").performClick()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
        composeTestRule.onNodeWithTag("edit").performClick()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
        composeTestRule.onNodeWithTag("edit").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
        composeTestRule.onNodeWithTag("toggle").performClick()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
        composeTestRule.onNodeWithTag("edit").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
        composeTestRule.onNodeWithTag("edit").performClick()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
        composeTestRule.onNodeWithTag("toggle").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)

        composeTestRule.onNodeWithTag("edit").performClick()
        composeTestRule.onNodeWithText("Focus").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
        composeTestRule.onNodeWithTag("toggle").performClick()
        composeTestRule.onNodeWithText("Focus").performClick()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
    }

    @Test
    fun exposedDropdownMenuFilterTest() {
        composeTestRule.onNodeWithTag("edit").performTextInput("A")
        composeTestRule.onNodeWithText("Abb").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abc").assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)

        composeTestRule.onNodeWithTag("edit").performTextInput("b")
        composeTestRule.onNodeWithText("Abb").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abc").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)

        composeTestRule.onNodeWithTag("edit").performTextInput("D")
        composeTestRule.onAllNodesWithText("Abb").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("Abc").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("A").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("B").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("C").assertCountEquals(0)
    }

}