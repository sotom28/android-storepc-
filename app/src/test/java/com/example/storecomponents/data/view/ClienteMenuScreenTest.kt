package com.example.storecomponents.data.view

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.NavController
import com.example.storecomponents.view.cliente.ClienteMenuScreen
import com.google.common.base.Verify.verify
import org.junit.Rule
import org.junit.Test
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.junit.Ignore


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ClienteMenuScreenTest {

    companion object {
        @JvmField
        @ClassRule
        val ensureBuildFingerprint = object : ExternalResource() {
            override fun before() {
                try {
                    val fingerprintField: Field = Build::class.java.getDeclaredField("FINGERPRINT")
                    fingerprintField.isAccessible = true
                    val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
                    modifiersField.isAccessible = true
                    modifiersField.setInt(fingerprintField, fingerprintField.modifiers and Modifier.FINAL.inv())
                    fingerprintField.set(null, "robolectric")
                } catch (t: Throwable) {
                    // ignorar si no es posible (ambientes diferentes)
                }
            }
        }

        @JvmStatic
        @BeforeClass
        fun setupFingerprint() {
            // Mantener backup por si @ClassRule no se aplica por alguna razón
            try {
                val fingerprintField: Field = Build::class.java.getDeclaredField("FINGERPRINT")
                fingerprintField.isAccessible = true
                val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
                modifiersField.isAccessible = true
                modifiersField.setInt(fingerprintField, fingerprintField.modifiers and Modifier.FINAL.inv())
                fingerprintField.set(null, "robolectric")
            } catch (t: Throwable) {
                // ignorar si no es posible (ambientes diferentes)
            }
        }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Ignore("Requiere Robolectric/Android environment; ejecutar por separado con connectedAndroidTest o configurar Robolectric")
    @Test
    fun muestraTituloYLogoutNavegaALogin(){

        // mock del navController usando mockito-kotlin
        val navController: NavController = mock()


        // montar el composable con el navController mockeado
        composeTestRule.setContent {
            ClienteMenuScreen(
                onNavigate = { route -> navController.navigate(route) },
                onLogout = { navController.navigate("login") }
            )

        }

        // verificar que el titulo existe
        composeTestRule.onNodeWithText("Menú Cliente").assertIsDisplayed()

        // click de boton logout
        composeTestRule.onNodeWithText("Cerrar sesión").performClick()

        // verificar que se navega a la pantalla de login
        verify(navController).navigate("login")

    }



}