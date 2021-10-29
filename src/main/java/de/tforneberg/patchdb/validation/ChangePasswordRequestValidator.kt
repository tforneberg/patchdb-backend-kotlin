package de.tforneberg.patchdb.validation

import de.tforneberg.patchdb.model.dto.ChangePasswordRequestData
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator

@Component
class ChangePasswordRequestValidator : Validator {
    override fun supports(aClass: Class<*>): Boolean {
        return ChangePasswordRequestData::class.java == aClass
    }

    override fun validate(o: Any, errors: Errors) {
        val req = o as ChangePasswordRequestData
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
    }
}