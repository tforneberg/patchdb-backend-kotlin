package de.tforneberg.patchdb.model.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ResetPasswordRequestData(
    @field:NotNull 
    @field:NotEmpty
    var email: String = "",

    @field:NotNull
    @field:NotEmpty
    var recaptchaToken: String = ""
)