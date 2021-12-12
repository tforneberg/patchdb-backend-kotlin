package de.tforneberg.patchdb.model.usermgmt.token

import de.tforneberg.patchdb.model.User
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class ResetPasswordToken(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    val token: String? = null,

    @field:OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @field:JoinColumn(nullable = false, name = "user_id")
    val user: User? = null,

    val expiryDate: Date? = null) {

    constructor(token: String?, user: User?, expiryTimeInMinutes: Int = 1440) : this(
        token = token,
        user = user,
        expiryDate = calculateExpiryDate(expiryTimeInMinutes)
    )

    companion object {
        private fun calculateExpiryDate(expiryTimeInMinutes:Int):Date {
            val cal:Calendar = Calendar.getInstance()
            cal.time = Timestamp(cal.time.time)
            cal.add(Calendar.MINUTE, expiryTimeInMinutes)
            return Date(cal.time.time)
        }
    }
}