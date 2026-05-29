package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.classes.Password
import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class UpdatePassword @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, currentPassword: Password, newPassword: Password): Result<Unit> {
        return userRepository.updatePassword(username, currentPassword, newPassword)
    }
}
