package com.paralearn.android.domain.classes

@JvmInline
value class Password(val value: String) {
    fun toInvisible(): String
    {
        return value.replace(Regex("."), "*");
    }

    fun isValid(): Boolean
    {
        return true
    }
}