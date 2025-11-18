package com.example.storecomponents.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.storecomponents.data.model.Usuarios
import org.json.JSONArray
import org.json.JSONObject

object LocalAuthStore {
    private const val PREFS_NAME = "local_auth_store"
    private const val KEY_USERS = "users_json"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun isInitialized(): Boolean = prefs != null

    fun saveUsers(users: List<Usuarios>) {
        val p = prefs ?: return
        val ja = JSONArray()
        for (u in users) {
            val jo = JSONObject()
            jo.put("id", u.id)
            jo.put("nombre", u.nombre)
            jo.put("correo", u.correo)
            jo.put("role", u.role.name)
            jo.put("password", u.password ?: "")
            jo.put("confirmarPassword", u.confirmarPassword ?: "")
            jo.put("direccion", u.direccion ?: "")
            ja.put(jo)
        }
        p.edit().putString(KEY_USERS, ja.toString()).apply()
    }

    fun loadUsers(): List<Usuarios> {
        val p = prefs ?: return emptyList()
        val raw = p.getString(KEY_USERS, null) ?: return emptyList()
        return try {
            val ja = JSONArray(raw)
            val list = mutableListOf<Usuarios>()
            for (i in 0 until ja.length()) {
                val jo = ja.getJSONObject(i)
                val id = jo.optInt("id")
                val nombre = jo.optString("nombre", "")
                val correo = jo.optString("correo", "")
                val roleStr = jo.optString("role", "CLIENT")
                val password = jo.optString("password", "")
                val confirmar = jo.optString("confirmarPassword", "")
                val direccion = jo.optString("direccion", "")
                val role = try { com.example.storecomponents.data.model.Userole.valueOf(roleStr) } catch (e: Exception) { com.example.storecomponents.data.model.Userole.CLIENT }
                val u = Usuarios(id = id, nombre = nombre, correo = correo, role = role, password = password, confirmarPassword = confirmar, direccion = direccion)
                list.add(u)
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveCurrentUserId(id: Int?) {
        val p = prefs ?: return
        if (id == null) p.edit().remove(KEY_CURRENT_USER_ID).apply() else p.edit().putInt(KEY_CURRENT_USER_ID, id).apply()
    }

    fun loadCurrentUserId(): Int? {
        val p = prefs ?: return null
        return if (p.contains(KEY_CURRENT_USER_ID)) p.getInt(KEY_CURRENT_USER_ID, -1) else null
    }
}

