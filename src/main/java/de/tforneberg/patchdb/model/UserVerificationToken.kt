package de.tforneberg.patchdb.model

import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
data class UserVerificationToken(
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Id
        var id: Int? = null,

        var token: String? = null,

        @field:OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
        @field:JoinColumn(nullable = false, name = "user_id")
        var user: User? = null,

        var expiryDate: Date? = null) {

    constructor(token: String?, user: User?, expiryTimeInMinutes: Int) : this(
            token = token,
            user = user,
            expiryDate = calculateExpiryDate(expiryTimeInMinutes)
    )

    companion object {
        private fun calculateExpiryDate(expiryTimeInMinutes:Int):Date {
            val cal:Calendar = Calendar.getInstance();
            cal.time = Timestamp(cal.time.time)
            cal.add(Calendar.MINUTE, expiryTimeInMinutes);
            return Date(cal.time.time);
        }
    }
}