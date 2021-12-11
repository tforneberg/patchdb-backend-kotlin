package de.tforneberg.patchdb.model.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class RegisterRequestData(
    @field:NotNull 
    @field:NotEmpty
    var email: String = "",

    @field:NotNull
    @field:NotEmpty
    var name: String = "",

    @field:NotNull
    @field:NotEmpty
    var password: String = "",

    @field:NotNull
    @field:NotEmpty
    var password2: String = "",

    @field:NotNull
    @field:NotEmpty
    var acceptedTerms: Boolean = false,

    @field:NotNull
    @field:NotEmpty
    var recaptchaToken: String = ""
)