package de.tforneberg.patchdb.event.listener

import de.tforneberg.patchdb.event.ResetPasswordEvent
import de.tforneberg.patchdb.model.usermgmt.token.ResetPasswordToken
import de.tforneberg.patchdb.repo.ResetPasswordTokenRepository
import org.springframework.context.ApplicationListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Component
class ResetPasswordEventListener(
    private val resetPasswordTokenRepository: ResetPasswordTokenRepository,
    private val mailSender: JavaMailSender) : ApplicationListener<ResetPasswordEvent> {
    override fun onApplicationEvent(event: ResetPasswordEvent) {
        confirmPasswordReset(event)
    }

    private fun confirmPasswordReset(event: ResetPasswordEvent) {
        val resetPasswordToken = ResetPasswordToken(UUID.randomUUID().toString(), event.user)
        resetPasswordTokenRepository.save(resetPasswordToken)

        val recipientAddress: String? = event.user.email
        val subject = "Password reset Confirmation"

        //val message: String = messageSource.getMessage("message.regSucc", null, event.locale)
        val message = "Please use the following link to reset your password:"
        val tokenUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/api/users/passwordResetConfirmation?token=" + resetPasswordToken.token

        val email = SimpleMailMessage()
        email.setFrom("test@patchdb.de")
        email.setTo(recipientAddress)
        email.setSubject(subject)
        email.setText("$message\r\n$tokenUrl")

        mailSender.send(email)
    }
}