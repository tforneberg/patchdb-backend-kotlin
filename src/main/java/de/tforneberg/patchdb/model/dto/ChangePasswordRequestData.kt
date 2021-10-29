package de.tforneberg.patchdb.model.dto

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ChangePasswordRequestData(
    @field:NotNull
    @field:NotEmpty
    var password: String = "",

    @field:NotNull
    @field:NotEmpty
    var password2: String = "",
)