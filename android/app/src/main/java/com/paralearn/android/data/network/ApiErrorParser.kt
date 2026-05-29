package com.paralearn.android.data.network

import com.google.gson.JsonParser

object ApiErrorParser {
    fun messageFrom(body: String?, fallback: String): String {
        if (body.isNullOrBlank()) return fallback
        return try {
            val json = JsonParser.parseString(body.trim()).asJsonObject
            json.get("error")?.takeIf { !it.isJsonNull }?.asString
                ?: json.get("message")?.takeIf { !it.isJsonNull }?.asString
                ?: body.trim()
        } catch (_: Exception) {
            body.trim()
        }
    }
}

fun <T> retrofit2.Response<T>.apiErrorMessage(fallback: String): String =
    ApiErrorParser.messageFrom(errorBody()?.string(), fallback)
