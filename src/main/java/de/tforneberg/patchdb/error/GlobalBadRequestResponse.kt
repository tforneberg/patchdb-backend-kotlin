package de.tforneberg.patchdb.error

import org.springframework.validation.BindingResult

class GlobalBadRequestResponse(private val origin: String?, private val code: String?) {

    constructor(result: BindingResult) : this(
            result.fieldError?.field ?: result.globalError?.code,
            result.fieldError?.code
    )
}
