package de.tforneberg.patchdb.model

import javax.persistence.Transient

abstract class UpdatableObject(
    @field:Transient
    var operation: HttpPatchOperation? = null,

    @field:Transient
    var path: String? = null,

    @field:Transient
    var value: String? = null
) {
    enum class HttpPatchOperation {
        add, remove, replace
    }
}