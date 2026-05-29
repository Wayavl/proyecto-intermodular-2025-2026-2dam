package com.paralearn.android.domain.use_case.user

import com.paralearn.android.domain.model.User
import com.paralearn.android.domain.repository.UserRepository
import javax.inject.Inject

class GetProfile @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): Result<User> {
        return userRepository.getProfile(username)
    }
}
