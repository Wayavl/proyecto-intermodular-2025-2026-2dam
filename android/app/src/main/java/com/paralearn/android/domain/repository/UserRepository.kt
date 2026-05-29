package com.paralearn.android.domain.repository

import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.classes.Password
import com.paralearn.android.domain.model.Session
import com.paralearn.android.domain.model.User

interface UserRepository {
    suspend fun register(username: String, email: Email, password: Password): Result<Session>
    suspend fun login(username: String, password: Password): Result<Session>
    suspend fun getProfile(username: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun updateUsername(username: String, newUsername: String): Result<Unit>
    suspend fun updateEmail(username: String, newEmail: Email): Result<Unit>
    suspend fun updatePassword(username: String, currentPassword: Password, newPassword: Password): Result<Unit>
    suspend fun deleteUser(username: String): Result<Unit>
}
