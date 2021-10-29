package de.tforneberg.patchdb.repo.utils

import de.tforneberg.patchdb.repo.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserUtils(private val repo: UserRepository) {
    fun mapIDtoUsername(id: Int): String? {
        return repo.findByIdOrNull(id)?.name
    }
}