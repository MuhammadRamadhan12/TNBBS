package com.app.tnbbs.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore dengan nama "user"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

// Data class untuk menyimpan data pengguna
data class User(
    val userId: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val profilePicture: String,
    val role: String
)


class SharedPref(private val context: Context) {

    // Deklarasi key untuk setiap data pengguna
    private val userId = stringPreferencesKey("userId")
    private val username = stringPreferencesKey("username")
    private val firstName = stringPreferencesKey("firstName")
    private val lastName = stringPreferencesKey("lastName")
    private val email = stringPreferencesKey("email")
    private val phoneNumber = stringPreferencesKey("phoneNumber")
    private val password = stringPreferencesKey("password")
    private val profilePicture = stringPreferencesKey("profilePicture")
    private val role = stringPreferencesKey("role")

    // Fungsi untuk menyimpan data pengguna ke DataStore
    suspend fun saveUserData(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        profilePicture: String,
        role: String
    ) {
        context.dataStore.edit {
            it[this.userId] = userId
            it[this.username] = username
            it[this.firstName] = firstName
            it[this.lastName] = lastName
            it[this.email] = email
            it[this.phoneNumber] = phoneNumber
            it[this.password] = password
            it[this.profilePicture] = profilePicture
            it[this.role] = role
        }
    }

    // Fungsi untuk memperbarui data pengguna di DataStore
    suspend fun updateUserDatas(
        userId: String,
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password: String,
        profilePicture: String
    ) {
        context.dataStore.edit {
            it[this.userId] = userId
            it[this.username] = username
            it[this.firstName] = firstName
            it[this.lastName] = lastName
            it[this.email] = email
            it[this.phoneNumber] = phoneNumber
            it[this.password] = password
            it[this.profilePicture] = profilePicture
        }
    }

    // Fungsi untuk mengambil userId dari DataStore
    val getUserId: Flow<String> = context.dataStore.data
        .map { it[userId] ?: "Undefined" }

    val getUserName: Flow<String> = context.dataStore.data
        .map { it[username] ?: "Undefined" }

    val getFirstName: Flow<String> = context.dataStore.data
        .map { it[firstName] ?: "Undefined" }

    val getLastName: Flow<String> = context.dataStore.data
        .map { it[lastName] ?: "Undefined" }

    val getEmail: Flow<String> = context.dataStore.data
        .map { it[email] ?: "Undefined" }

    val getPhoneNumber: Flow<String> = context.dataStore.data
        .map { it[phoneNumber] ?: "Undefined" }

    val getPassword: Flow<String> = context.dataStore.data
        .map { it[password] ?: "Undefined" }

    val getProfilePicture: Flow<String> = context.dataStore.data
        .map { it[profilePicture] ?: "Undefined" }

    val getRole: Flow<String> = context.dataStore.data
        .map { it[role] ?: "Undefined" }

    // Fungsi untuk mengambil semua data pengguna dari DataStore
    val getAllUserData: Flow<User> = context.dataStore.data
        .map { preferences ->
            User(
                userId = preferences[userId] ?: "Undefined",
                username = preferences[username] ?: "Undefined",
                firstName = preferences[firstName] ?: "Undefined",
                lastName = preferences[lastName] ?: "Undefined",
                email = preferences[email] ?: "Undefined",
                phoneNumber = preferences[phoneNumber] ?: "Undefined",
                password = preferences[password] ?: "Undefined",
                profilePicture = preferences[profilePicture] ?: "Undefined",
                role = preferences[role] ?: "Undefined"
            )
        }

    // Fungsi untuk menghapus sesi pengguna (menghapus semua data pengguna dari DataStore)
    suspend fun removeSession() {
        context.dataStore.edit {
            it.remove(userId)
            it.remove(username)
            it.remove(firstName)
            it.remove(lastName)
            it.remove(email)
            it.remove(phoneNumber)
            it.remove(password)
            it.remove(profilePicture)
            it.remove(role)
        }
    }
}
