package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.controller.Constants
import de.tforneberg.patchdb.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional

interface UserRepository : JpaRepository<User, Int> {

    fun findByName(name: String): User?

    fun findByEmail(email: String): User?

    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    override fun deleteById(id: Int)

    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    override fun delete(user: User)

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO collections (patch_id, user_id) VALUES (?1, ?2)", nativeQuery = true)
    fun insertIntoCollection(patchId: Int, userId: Int)

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM collections WHERE patch_id = ?1 AND user_id = ?2", nativeQuery = true)
    fun deletePatchFromCollection(patchId: Int, userId: Int) //TODO return value boolean or Integer?
}