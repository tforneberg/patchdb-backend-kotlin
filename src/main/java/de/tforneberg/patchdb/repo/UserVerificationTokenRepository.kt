package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.model.User
import de.tforneberg.patchdb.model.usermgmt.token.UserVerificationToken
import org.springframework.data.jpa.repository.JpaRepository

interface UserVerificationTokenRepository : JpaRepository<UserVerificationToken, Int> {
    fun findByToken(token: String): UserVerificationToken?

    fun findByUser(user: User): UserVerificationToken?
}