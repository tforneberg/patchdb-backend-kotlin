package de.tforneberg.patchdb.event

import de.tforneberg.patchdb.model.User
import org.springframework.context.ApplicationEvent
import java.util.*

data class RegistrationCompleteEvent(
        val user: User,
        val locale: Locale) : ApplicationEvent(user)