package de.tforneberg.patchdb.validation

import de.tforneberg.patchdb.model.dto.RegisterRequestData
import de.tforneberg.patchdb.repo.UserRepository
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

@Component
class RegisterRequestValidator(private val userRepo: UserRepository) : Validator {

    override fun supports(aClass: Class<*>): Boolean {
        return RegisterRequestData::class.java == aClass
    }

    override fun validate(o: Any, errors: Errors) {
        val req = o as RegisterRequestData
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "notEmpty")
        if (req.name.length < 6 || req.name.length > 32) {
            errors.rejectValue("name", "size")
        }
        userRepo.findByName(req.name)?.let {
            errors.rejectValue("name", "duplicate")
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "notEmpty")
        userRepo.findByEmail(req.email)?.let {
            errors.rejectValue("email", "duplicate")
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "notEmpty")
        if (req.password.length < 6 || req.password.length > 32) {
            errors.rejectValue("password", "size")
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password2", "notEmpty")
        if (req.password.length < 6 || req.password.length > 32) {
            errors.rejectValue("password2", "size")
        }
        if (req.password != req.password2) {
            errors.rejectValue("password2", "passwordConfirm")
        }

        if (!req.acceptedTerms) {
            errors.rejectValue("acceptedTerms", "notAccepted")
        }
    }
}