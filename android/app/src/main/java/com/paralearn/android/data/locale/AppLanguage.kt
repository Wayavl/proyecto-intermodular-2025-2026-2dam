package com.paralearn.android.data.locale

import java.util.Locale

object AppLanguage {
    const val ENGLISH = "en"
    const val SPANISH = "es"
    const val CHINESE = "zh"
    const val DEFAULT = ENGLISH

    val supported: List<String> = listOf(ENGLISH, SPANISH, CHINESE)

    fun isSupported(code: String): Boolean = code in supported

    fun normalize(code: String?): String = code?.takeIf { isSupported(it) } ?: DEFAULT

    fun fromDeviceLocale(): String = normalize(Locale.getDefault().language)
}
