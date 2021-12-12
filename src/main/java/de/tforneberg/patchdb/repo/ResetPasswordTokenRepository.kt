package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.model.User
import de.tforneberg.patchdb.model.usermgmt.token.ResetPasswordToken
import org.springframework.data.jpa.repository.JpaRepository

interface ResetPasswordTokenRepository : JpaRepository<ResetPasswordToken, Int> {
    fun findByToken(token: String): ResetPasswordToken?

    fun findByUser(user: User): ResetPasswordToken?
}