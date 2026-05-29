package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.classes.Password
import com.paralearn.android.domain.model.Session
import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class LogIn @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(password: String, actor: String) : Result<Session> {
        return userRepository.login(actor, Password(password))
    }
}