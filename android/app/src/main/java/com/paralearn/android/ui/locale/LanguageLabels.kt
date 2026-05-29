package com.paralearn.android.ui.locale

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paralearn.android.R
import com.paralearn.android.data.locale.AppLanguage

@StringRes
fun languageLabelRes(code: String): Int = when (code) {
    AppLanguage.SPANISH -> R.string.lang_spanish
    AppLanguage.CHINESE -> R.string.lang_chinese
    else -> R.string.lang_english
}

@Composable
fun languageLabel(code: String): String = stringResource(languageLabelRes(code))
