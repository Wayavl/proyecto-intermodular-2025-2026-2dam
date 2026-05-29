package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.classes.Password
import com.paralearn.android.domain.model.Session
import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class Register @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(password: String, username: String, email: String) : Result<Session> {
        return userRepository.register(username, Email(email), Password(password))
    }
}