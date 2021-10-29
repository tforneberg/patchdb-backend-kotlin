package de.tforneberg.patchdb.error

import org.springframework.validation.BindingResult

class BadRequestException(val bindingResult: BindingResult) : RuntimeException() {

    companion object {
        private const val serialVersionUID = -2146006695243268255L
    }
}