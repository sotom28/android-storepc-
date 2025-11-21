package com.example.storecomponents.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


data class Purchase(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val amount: Double = 0.0,
    val date: String = ""
)

data class User(
    val name: String = "",
    val correo: String = "",
    val phone: String = "",
    val direccion: String = "",
    val photo: Bitmap? = null,
    val purchases: List<Purchase> = emptyList()
)

class PerfilviewModel(private val repo: PerfilRepository) : ViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        // Cargar user guardado
        viewModelScope.launch {
            val loaded = repo.loadUser()
            _user.value = loaded
        }
    }

    private fun persistCurrent() {
        viewModelScope.launch {
            repo.saveUser(_user.value)
        }
    }

    /// Actualizar campos del usuario (CRUD básico con persistencia)
    fun updateUserFields(name: String, correo: String, phone: String, direccion: String) {
        viewModelScope.launch {
            _user.update { it.copy(name = name, correo = correo, phone = phone, direccion = direccion) }
            persistCurrent()
        }
    }

    fun setUser(name: String, email: String, phone: String, photo: Bitmap?) {
        viewModelScope.launch {
            _user.value = User(name, email, phone, "", photo)
            persistCurrent()
        }
    }

    fun updatePhoto(bitmap: Bitmap){
        viewModelScope.launch {
            _user.update { it.copy(photo = bitmap) }
            persistCurrent()
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            _user.value = User()
            repo.clearUser()
        }
    }

    fun addPurchase(title: String, amount: Double, date: String) {
        viewModelScope.launch {
            val purchase = Purchase(title = title, amount = amount, date = date)
            _user.update { it.copy(purchases = it.purchases + purchase) }
            repo.addPurchase(purchase)
        }
    }

    fun removePurchase(purchaseId: String) {
        viewModelScope.launch {
            _user.update { it.copy(purchases = it.purchases.filterNot { p -> p.id == purchaseId }) }
            repo.removePurchase(purchaseId)
        }
    }

    fun clearPurchases() {
        viewModelScope.launch {
            _user.update { it.copy(purchases = emptyList()) }
            repo.clearPurchases()
        }
    }

    class Factory(private val repo: PerfilRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PerfilviewModel(repo) as T
        }
    }
}
