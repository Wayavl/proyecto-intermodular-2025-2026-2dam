package com.paralearn.android.domain.exceptions

sealed class SessionExceptions() {
    class Unauthorized() : SessionExceptions()
}