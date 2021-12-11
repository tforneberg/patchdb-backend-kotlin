package de.tforneberg.patchdb.event.listener

import de.tforneberg.patchdb.event.OnRegistrationCompleteEvent
import de.tforneberg.patchdb.model.UserVerificationToken
import de.tforneberg.patchdb.repo.UserVerificationTokenRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.MessageSource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegistrationCompleteListener(
        private val userVerificationTokenRepository: UserVerificationTokenRepository,
        private val messageSource: MessageSource,
        private val mailSender: JavaMailSender)
    : ApplicationListener<OnRegistrationCompleteEvent> {

    override fun onApplicationEvent(event: OnRegistrationCompleteEvent) {
        confirmRegistration(event)
    }

    private fun confirmRegistration(event: OnRegistrationCompleteEvent) {
        val userVerificationToken = UserVerificationToken(UUID.randomUUID().toString(), event.user, 1440)
        userVerificationTokenRepository.save(userVerificationToken)

        val recipientAddress: String? = event.user.email
        val subject = "Registration Confirmation"
        val confirmationUrl: String = event.appUrl + "/registrationConfirmation?token=" + userVerificationToken.token
        //val message: String = messageSource.getMessage("message.regSucc", null, event.locale)
        val message = "Deine Registrierung war erfolgreich. Bitte klicke auf den Link um diese zu bestätigen:"

        val email = SimpleMailMessage()
        email.setTo("test@patchdb.de")
        email.setTo(recipientAddress)
        email.setSubject(subject)
        email.setText("$message\r\nhttp://localhost:8080$confirmationUrl") //TODO add proper url

         mailSender.send(email)
    }
}