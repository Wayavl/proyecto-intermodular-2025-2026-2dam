package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUsername @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, newUsername: String): Result<Unit> {
        return userRepository.updateUsername(username, newUsername)
    }
}
