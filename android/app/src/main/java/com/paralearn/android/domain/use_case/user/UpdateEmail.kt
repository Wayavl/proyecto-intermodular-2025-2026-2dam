package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class UpdateEmail @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, newEmail: String): Result<Unit> {
        return userRepository.updateEmail(username, Email(newEmail))
    }
}
