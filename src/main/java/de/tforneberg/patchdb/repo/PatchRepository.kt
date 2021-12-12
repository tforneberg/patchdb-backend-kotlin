package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.model.Patch
import de.tforneberg.patchdb.model.Patch.PatchState
import de.tforneberg.patchdb.model.Patch.PatchType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PatchRepository : JpaRepository<Patch, Int> {
    @Query(value = "SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)",
            nativeQuery = true, countQuery = "SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1)")
    fun findPatchesByUserId(id: Int, page: Pageable): Page<Patch> //TODO convert to JPQL ... how with collection?

    @Query(value = "SELECT * FROM patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2",
            nativeQuery = true, countQuery = "SELECT count(*) FROM  patches WHERE id IN (SELECT patch_id FROM collections WHERE user_id = ?1) AND state = ?2")
    fun findPatchesByUserIdAndWithState(id: Int, state: String, page: Pageable): Page<Patch> //TODO convert to JPQL ... how with collection?

    fun findByIdAndState(id: Int, state: PatchState): Patch?

    fun findByNameContainingIgnoreCaseAndState(name: String, state: PatchState, pageable: Pageable): Page<Patch>

    fun findByState(state: PatchState, page: Pageable): Page<Patch>

    @Query(value = "SELECT p FROM Patch AS p WHERE p.band.id = ?1 AND p.state = ?2",
            countQuery = "SELECT COUNT(p) FROM Patch AS p WHERE p.band.id = ?1 AND p.state = ?2")
    fun findByBandIdAndWithState(bandId: Int, state: String, page: Pageable): Page<Patch>

    @Query(value = "SELECT p FROM Patch AS p WHERE p.userInserted.id = ?1 AND p.state = ?2",
            countQuery = "SELECT COUNT(p) FROM Patch AS p WHERE p.userInserted.id = ?1 AND p.state = ?2")
    fun findPatchesByCreatorIdAndWithState(id: Int, state: String, page: Pageable): Page<Patch>

    @Query(value = "SELECT p FROM Patch AS p WHERE p.type = ?1 AND p.state = ?2",
            countQuery = "SELECT COUNT(p) FROM Patch AS p WHERE p.type = ?1 AND p.state = ?2")
    fun findPatchesByTypeAndWithState(type: PatchType, state: String, page: Pageable): Page<Patch>
}