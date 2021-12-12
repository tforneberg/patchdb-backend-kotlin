package de.tforneberg.patchdb.event

import de.tforneberg.patchdb.model.User
import org.springframework.context.ApplicationEvent

data class ResetPasswordEvent(var user: User, ) : ApplicationEvent(user)