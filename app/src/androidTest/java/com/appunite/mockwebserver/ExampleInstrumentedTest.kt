package com.appunite.mockwebserver

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appunite.mockwebserver_extension.MockWebServerRule
import com.appunite.mockwebserver_extension.util.jsonResponse
import com.appunite.mockwebserver_extension.util.path
import com.appunite.mockwebserver_extension.util.url
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule(order = 0)
    val composeTestRule = createAndroidComposeRule(MainActivity::class.java)

    @get:Rule(order = 1)
    var mockWebServer: MockWebServerRule = MockWebServerRule()

    @Test
    fun whenClickOnButton_showRandomCatFact() {
        mockWebServer.register {
            expectThat(it).url.path.isEqualTo("/fact")
            jsonResponse("""{"fact": "Example fact about your cat."}""")
        }

        composeTestRule.onNodeWithText("Get fact!").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Example fact about your cat.").assertIsDisplayed()
    }
}
