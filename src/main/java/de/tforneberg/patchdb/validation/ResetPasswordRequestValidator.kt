package de.tforneberg.patchdb.validation

import de.tforneberg.patchdb.model.dto.ResetPasswordRequestData
import de.tforneberg.patchdb.repo.UserRepository
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

@Component
class ResetPasswordRequestValidator(private val userRepo: UserRepository) : Validator {

    override fun supports(aClass: Class<*>): Boolean {
        return ResetPasswordRequestData::class.java == aClass
    }

    override fun validate(o: Any, errors: Errors) {
        val req = o as ResetPasswordRequestData

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "notEmpty")
        if (userRepo.findByEmail(req.email) == null) {
            errors.rejectValue("email", "unknown")
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recaptchaToken", "notEmpty")
    }
}