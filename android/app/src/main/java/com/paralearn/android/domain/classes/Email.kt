package com.paralearn.android.domain.classes

@JvmInline()
value class Email(val value: String) {
    fun isValid(): Boolean {
        return true
    }
}