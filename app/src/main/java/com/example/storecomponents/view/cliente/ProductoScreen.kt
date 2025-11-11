package com.example.storecomponents.view.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.view.components.ProductoCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Mock ViewModel for demonstration
class ProductViewModel : ViewModel() {
    private val _productos = MutableStateFlow(sampleProducts())
    val productos: StateFlow<List<Producto>> = _productos

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
        filterProducts()
    }

    private fun filterProducts() {
        val query = _searchQuery.value.lowercase()
        val category = _selectedCategory.value

        _productos.value = sampleProducts().filter { producto ->
            val matchesQuery = producto.nombre.lowercase().contains(query) ||
                    producto.descripcion.lowercase().contains(query)
            val matchesCategory = category == null || producto.categoria == category
            matchesQuery && matchesCategory
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(viewModel: ProductViewModel = viewModel()) {
    val productos by viewModel.productos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = sampleProducts().map { it.categoria }.distinct()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Productos") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(searchQuery, viewModel::onSearchQueryChange)
            FilterChips(categories, selectedCategory, viewModel::onCategorySelected)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(productos) {
                    ProductoCard(producto = it, onClick = { /* Handle click */ })
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Buscar productos") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(categories: List<String>, selectedCategory: String?, onCategorySelected: (String?) -> Unit) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        InputChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Todos") }
        )
        Spacer(Modifier.width(8.dp))
        categories.forEach { category ->
            InputChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

fun sampleProducts() = listOf(
    Producto(nombre = "Laptop Gamer", descripcion = "Potente laptop para juegos", precio = 1500.0, stock = 10, categoria = "Portátiles", imagenUrl = ""),
    Producto(nombre = "Mouse Inalámbrico", descripcion = "Mouse ergonómico", precio = 25.0, stock = 100, categoria = "Accesorios", imagenUrl = ""),
    Producto(nombre = "Teclado Mecánico", descripcion = "Teclado con switches Cherry MX", precio = 120.0, stock = 30, categoria = "Accesorios", imagenUrl = ""),
    Producto(nombre = "Monitor 4K", descripcion = "Monitor de 27 pulgadas", precio = 450.0, stock = 15, categoria = "Monitores", imagenUrl = ""),
    Producto(nombre = "Silla Gamer", descripcion = "Silla ergonómica para largas sesiones", precio = 250.0, stock = 20, categoria = "Mobiliario", imagenUrl = ""),
    Producto(nombre = "MacBook Pro", descripcion = "Portátil de Apple", precio = 2500.0, stock = 5, categoria = "Portátiles", imagenUrl = "")
)
