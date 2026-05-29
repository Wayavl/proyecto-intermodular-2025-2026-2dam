package com.paralearn.android.data.locale

import android.content.Context
import android.content.res.Configuration
import com.paralearn.android.data.session.SessionStorage
import java.util.Locale

object LocaleHelper {
    fun wrap(context: Context): Context {
        val language = SessionStorage.readLanguageCode(context)
        return applyLocale(context, language)
    }

    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = localeForCode(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun localeForCode(languageCode: String): Locale = when (AppLanguage.normalize(languageCode)) {
        AppLanguage.SPANISH -> Locale.forLanguageTag("es")
        AppLanguage.CHINESE -> Locale.SIMPLIFIED_CHINESE
        else -> Locale.ENGLISH
    }
}
