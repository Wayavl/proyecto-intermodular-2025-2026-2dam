package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.*
import com.paralearn.android.data.network.SessionCookieJar
import com.paralearn.android.data.network.UserAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.classes.Password
import com.paralearn.android.domain.model.Session
import com.paralearn.android.domain.model.User
import com.paralearn.android.domain.repository.UserRepository
import java.time.Instant
import javax.inject.Inject

class HybridUserRepository @Inject constructor(
    private val userAPI: UserAPI,
    private val sessionManager: SessionManager,
    private val sessionCookieJar: SessionCookieJar
) : UserRepository {

    private fun parseDateString(dateStr: String?): Instant? {
        if (dateStr == null) return null
        return try {
            Instant.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = this.id ?: this.legacyId,
            username = this.username,
            email = this.email?.let { Email(it) },
            joinDate = parseDateString(this.joinDate),
            premiumExpirationDate = parseDateString(this.premiumExpirationDate),
            streak = this.streak,
            lastStreak = parseDateString(this.lastStreak)
        )
    }

    private fun extractSessionToken(headers: okhttp3.Headers): Session {
        val cookies = headers.values("Set-Cookie")
        for (cookie in cookies) {
            val marker = "authorization="
            val index = cookie.indexOf(marker, ignoreCase = true)
            if (index >= 0) {
                val token = cookie.substring(index + marker.length).substringBefore(";").trim()
                if (token.isNotBlank()) {
                    return Session(token)
                }
            }
        }
        return Session("")
    }

    private fun resolveAuthToken(bodyToken: String?, headers: okhttp3.Headers): String? {
        return bodyToken?.takeIf { it.isNotBlank() }
            ?: extractSessionToken(headers).value.takeIf { it.isNotBlank() }
    }

    private fun persistAuthToken(token: String?) {
        if (!token.isNullOrBlank()) {
            sessionManager.setAuthToken(token)
        }
    }

    override suspend fun register(username: String, email: Email, password: Password): Result<Session> {
        return try {
            val response = userAPI.register(RegisterRequest(username, email.value, password.value))
            if (response.isSuccessful) {
                val userDto = response.body()?.user
                val sessionUsername = userDto?.username
                if (sessionUsername.isNullOrBlank()) {
                    return Result.failure(Exception("Registration succeeded but username was missing in the response"))
                }
                sessionManager.setSession(sessionUsername)
                (userDto.id ?: userDto.legacyId)?.let { sessionManager.setUserId(it) }
                val token = resolveAuthToken(response.body()?.accessToken, response.headers())
                persistAuthToken(token)
                Result.success(Session(token.orEmpty()))
            } else {
                Result.failure(Exception(response.apiErrorMessage("Registration failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(identifier: String, password: Password): Result<Session> {
        return try {
            val loginWithEmail = identifier.contains('@')
            val response = userAPI.login(
                LoginRequest(
                    username = if (loginWithEmail) null else identifier,
                    email = if (loginWithEmail) identifier else null,
                    password_plain = password.value
                )
            )
            if (response.isSuccessful) {
                val userDto = response.body()?.user
                val sessionUsername = userDto?.username
                if (sessionUsername.isNullOrBlank()) {
                    return Result.failure(Exception("Login succeeded but username was missing in the response"))
                }
                sessionManager.setSession(sessionUsername)
                (userDto.id ?: userDto.legacyId)?.let { sessionManager.setUserId(it) }
                val token = resolveAuthToken(response.body()?.accessToken, response.headers())
                persistAuthToken(token)
                Result.success(Session(token.orEmpty()))
            } else {
                Result.failure(Exception(response.apiErrorMessage("Login failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(username: String): Result<User> {
        return try {
            val response = userAPI.getProfile(username)
            val body = response.body()
            if (response.isSuccessful && body?.profile != null) {
                (body.profile.id ?: body.profile.legacyId)?.let { sessionManager.setUserId(it) }
                Result.success(body.profile.toDomain())
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get profile")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val response = userAPI.logout()
            if (response.isSuccessful) {
                sessionManager.clearSession()
                sessionCookieJar.clear()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Logout failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUsername(username: String, newUsername: String): Result<Unit> {
        return try {
            val response = userAPI.updateUsername(username, UpdateUsernameRequest(newUsername))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update username failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmail(username: String, newEmail: Email): Result<Unit> {
        return try {
            val response = userAPI.updateEmail(username, UpdateEmailRequest(newEmail.value))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update email failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(username: String, currentPassword: Password, newPassword: Password): Result<Unit> {
        return try {
            val response = userAPI.updatePassword(username, UpdatePasswordRequest(newPassword.value))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update password failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(username: String): Result<Unit> {
        return try {
            val response = userAPI.deleteUser(username)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete user failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}