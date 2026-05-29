package com.paralearn.android.domain.model
import com.paralearn.android.domain.classes.Email
import java.time.Instant

data class User(
    val id: String?,
    val username: String?,
    val email: Email?,
    val joinDate: Instant?,
    val premiumExpirationDate: Instant?,
    val streak: Int?,
    val lastStreak: Instant?
) {
    fun isPremium(now: Instant = Instant.now()): Boolean {
        return premiumExpirationDate != null && now.isBefore(premiumExpirationDate);
    }
}
