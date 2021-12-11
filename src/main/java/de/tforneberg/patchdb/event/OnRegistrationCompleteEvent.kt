package de.tforneberg.patchdb.event

import de.tforneberg.patchdb.model.User
import org.springframework.context.ApplicationEvent
import java.util.*

data class OnRegistrationCompleteEvent(
        var user: User,
        var locale: Locale,
        var appUrl: String) : ApplicationEvent(user)