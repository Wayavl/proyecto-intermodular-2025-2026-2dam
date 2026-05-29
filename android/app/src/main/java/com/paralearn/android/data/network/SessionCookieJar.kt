package com.paralearn.android.data.network

import com.paralearn.android.data.session.SessionManager
import java.util.concurrent.ConcurrentHashMap
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionCookieJar @Inject constructor(
    private val sessionManager: SessionManager
) : CookieJar {

    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            cookieStore[url.host] = cookies
        }
        cookies
            .firstOrNull { it.name.equals("authorization", ignoreCase = true) }
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?.let { sessionManager.setAuthToken(it) }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val stored = cookieStore[url.host]
        if (!stored.isNullOrEmpty()) {
            return stored
        }

        val token = sessionManager.authToken.value?.takeIf { it.isNotBlank() } ?: return emptyList()
        return listOf(
            Cookie.Builder()
                .name("authorization")
                .value(token)
                .domain(url.host)
                .path("/")
                .build()
        )
    }

    fun clear() {
        cookieStore.clear()
    }
}
