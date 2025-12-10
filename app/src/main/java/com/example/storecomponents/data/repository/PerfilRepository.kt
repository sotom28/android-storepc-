package com.example.storecomponents.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.storecomponents.data.model.Purchase
import com.example.storecomponents.data.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class PerfilRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("perfil_prefs", Context.MODE_PRIVATE)
    private val KEY_USER = "user_json"

    fun saveUser(user: User) {
        val json = JSONObject()
        json.put("name", user.name)
        json.put("correo", user.correo)
        json.put("phone", user.phone)
        json.put("direccion", user.direccion)

        // photo as base64
        user.photo?.let { bmp ->
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 90, baos)
            val bytes = baos.toByteArray()
            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            json.put("photo", base64)
        }

        // purchases
        val arr = JSONArray()
        user.purchases.forEach { p ->
            val pj = JSONObject()
            pj.put("id", p.id)
            pj.put("title", p.title)
            pj.put("amount", p.amount)
            pj.put("date", p.date)
            arr.put(pj)
        }
        json.put("purchases", arr)

        prefs.edit().putString(KEY_USER, json.toString()).apply()
    }

    fun loadUser(): User {
        val str = prefs.getString(KEY_USER, null) ?: return User()
        try {
            val json = JSONObject(str)
            val name = json.optString("name", "")
            val correo = json.optString("correo", "")
            val phone = json.optString("phone", "")
            val direccion = json.optString("direccion", "")

            var photo: Bitmap? = null
            if (json.has("photo")) {
                val base64 = json.optString("photo", "")
                if (base64.isNotEmpty()) {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }

            val purchases = mutableListOf<Purchase>()
            val arr = json.optJSONArray("purchases")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val pj = arr.optJSONObject(i)
                    if (pj != null) {
                        val id = pj.optString("id", "")
                        val title = pj.optString("title", "")
                        val amount = pj.optDouble("amount", 0.0)
                        val date = pj.optString("date", "")
                        purchases.add(
                            Purchase(
                                id = id,
                                title = title,
                                amount = amount,
                                date = date
                            )
                        )
                    }
                }
            }

            return User(
                name = name,
                correo = correo,
                phone = phone,
                direccion = direccion,
                photo = photo,
                purchases = purchases
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return User()
        }
    }

    fun clearUser() {
        prefs.edit().remove(KEY_USER).apply()
    }

    fun addPurchase(purchase: Purchase) {
        val user = loadUser()
        val updated = user.copy(purchases = user.purchases + purchase)
        saveUser(updated)
    }

    fun removePurchase(purchaseId: String) {
        val user = loadUser()
        val updated = user.copy(purchases = user.purchases.filterNot { it.id == purchaseId })
        saveUser(updated)
    }

    fun clearPurchases() {
        val user = loadUser()
        val updated = user.copy(purchases = emptyList())
        saveUser(updated)
    }
}