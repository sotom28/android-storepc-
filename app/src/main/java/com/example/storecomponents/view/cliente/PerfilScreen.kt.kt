package com.example.storecomponents.view.cliente

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.storecomponents.viewmodel.PerfilviewModel
import com.example.storecomponents.viewmodel.User
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

@Composable
fun PerfilScreen(viewModel: PerfilviewModel? = null, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher para escoger imagen desde galería (image/*)
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val stream = context.contentResolver.openInputStream(it)
                val bmp = BitmapFactory.decodeStream(stream)
                stream?.close()
                if (bmp != null && viewModel != null) {
                    // actualiza la foto en el viewModel
                    scope.launch { viewModel.updatePhoto(bmp) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher para tomar foto con la cámara (devuelve Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        bmp?.let {
            if (viewModel != null) {
                scope.launch { viewModel.updatePhoto(it) }
            }
        } ?: run {
            // opcional: mensaje si se canceló o falló
            Toast.makeText(context, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Observable user state (si no hay viewModel usamos un User vacío)
    val userState by viewModel?.user?.collectAsState(initial = User()) ?: remember { mutableStateOf(User()) }

    // Local editable copies
    var name by remember { mutableStateOf(TextFieldValue(userState.name)) }
    var correo by remember { mutableStateOf(TextFieldValue(userState.correo)) }
    var phone by remember { mutableStateOf(TextFieldValue(userState.phone)) }
    var direccion by remember { mutableStateOf(TextFieldValue(userState.direccion)) }

    // Cuando cambia el userState, actualizar los campos locales
    LaunchedEffect(userState) {
        name = TextFieldValue(userState.name)
        correo = TextFieldValue(userState.correo)
        phone = TextFieldValue(userState.phone)
        direccion = TextFieldValue(userState.direccion)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Mi Perfil", style = MaterialTheme.typography.titleLarge)
                OutlinedButton(onClick = onBack) {
                    Text("Volver")
                }
            }

            // Foto
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp), contentAlignment = Alignment.Center) {
                userState.photo?.let { bmp ->
                    Image(bitmap = bmp.asImageBitmap(), contentDescription = "Foto de perfil", modifier = Modifier.fillMaxHeight().aspectRatio(1f))
                } ?: run {
                    Box(modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Text(text = "Sin foto")
                    }
                }
            }

            // Botón para añadir/cambiar foto
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { cameraLauncher.launch(null) }) {
                        Text("Sacar foto")
                    }

                    Button(onClick = { imageLauncher.launch("image/*") }) {
                        Text("Agregar foto")
                    }
                }
            }

            // Campos editables
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            // Acciones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    if (viewModel == null) {
                        Toast.makeText(context, "Perfil no disponible", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    scope.launch {
                        viewModel.updateUserFields(name.text, correo.text, phone.text, direccion.text)
                        Toast.makeText(context, "Perfil guardado", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Guardar")
                }

                OutlinedButton(onClick = {
                    if (viewModel == null) {
                        Toast.makeText(context, "Perfil no disponible", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    scope.launch {
                        viewModel.deleteUser()
                        Toast.makeText(context, "Perfil eliminado", Toast.LENGTH_SHORT).show()
                    }
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors()) {
                    Text("Eliminar perfil")
                }
            }

            // Compras / historial
            Text(text = "Historial de Compras", style = MaterialTheme.typography.titleMedium)

            if (userState.purchases.isEmpty()) {
                Text(text = "No hay compras", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxHeight()) {
                    items(userState.purchases) { p ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = p.title, style = MaterialTheme.typography.titleSmall)
                                    Text(text = "Monto: ${p.amount}")
                                    Text(text = p.date, style = MaterialTheme.typography.bodySmall)
                                }
                                Row {
                                    IconButton(onClick = {
                                        if (viewModel == null) return@IconButton
                                        scope.launch { viewModel.removePurchase(p.id) }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar compra")
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { if (viewModel != null) scope.launch { viewModel.clearPurchases() } }) {
                                Text("Eliminar historial")
                            }
                        }
                    }
                }
            }
        }
    }
}