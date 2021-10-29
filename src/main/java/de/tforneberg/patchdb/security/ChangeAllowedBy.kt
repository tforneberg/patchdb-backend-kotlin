package de.tforneberg.patchdb.security

import de.tforneberg.patchdb.model.User.UserStatus

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ChangeAllowedBy(val roles: Array<UserStatus> = [UserStatus.user])