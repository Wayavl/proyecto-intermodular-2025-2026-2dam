package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class LogOut @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.logout()
    }
}
