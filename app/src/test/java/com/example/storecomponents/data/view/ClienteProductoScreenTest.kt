package com.example.storecomponents.data.view

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.storecomponents.navigation.AppNavGraph
import com.example.storecomponents.view.cliente.ClienteProductosScreen
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class ClienteProductoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Ignore("Test UI que requiere Robolectric/Android; ejecutar por separado o con AndroidComposeTestRule configurado")
    @Test
    fun navigateToProductDetail_whenDetailButtonIsClicked() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppNavGraph()
            ClienteProductosScreen(onNavigate = { route ->
                navController.navigate(route)
            })
        }

        // Wait for products to load (adjust delay as needed)
        Thread.sleep(2000)

        // Find the "Detalle" button and click it
        composeTestRule.onNodeWithText("Detalle").performClick()

        // Verify that we navigated to the detail screen
        composeTestRule.onNodeWithText("Detalle del Producto").assertIsDisplayed()
    }
}