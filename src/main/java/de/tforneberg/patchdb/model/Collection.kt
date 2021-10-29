package de.tforneberg.patchdb.model

import com.fasterxml.jackson.annotation.JsonView

data class Collection(
    @field:JsonView(Patch.DefaultView::class)
    var patches: List<Patch>? = null,

    @field:JsonView(User.DefaultView::class)
    var username: String? = null
)