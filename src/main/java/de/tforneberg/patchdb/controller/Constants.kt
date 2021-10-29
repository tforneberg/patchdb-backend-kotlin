package de.tforneberg.patchdb.controller

object Constants {
    const val ID_MAPPING = "/{id}"

    //Auth
    const val LOGGED_IN = "isAuthenticated()"
    const val AUTH_ADMIN_OR_MOD = "hasAuthority('admin') || hasAuthority('mod')"
    const val AUTH_ADMIN = "hasAuthority('admin')"
    const val AUTH_MOD = "hasAuthority('mod')"
    const val AUTH_ID_IS_OF_REQUESTING_USER = "@userUtils.mapIDtoUsername(#userId) == authentication.principal.username"

    //paging/sorting
    const val PAGE = "page"
    const val SIZE = "size"
    const val SORTBY = "sortBy"
    const val DIRECTION = "direction"
}