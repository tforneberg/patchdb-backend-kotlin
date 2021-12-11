package de.tforneberg.patchdb.service.mail

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@ConstructorBinding
@ConfigurationProperties(prefix = "spring.mail")
data class MailConfig(
        val host:String,
        val port:Int,
        val username:String,
        val password:String) {

    @Bean
    fun getMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = username
        mailSender.password = password

        mailSender.javaMailProperties["mail.transport.protocol"] = "smtp"
        mailSender.javaMailProperties["mail.smtp.auth"] = "true"
        mailSender.javaMailProperties["mail.smtp.starttls.enable"] = "true"
        mailSender.javaMailProperties["mail.debug"] = "true"
        return mailSender
    }
}