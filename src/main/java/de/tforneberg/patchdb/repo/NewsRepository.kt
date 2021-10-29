package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.model.News
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NewsRepository : JpaRepository<News, Int> {
    @Query(value = "SELECT n FROM News as n WHERE creator.id = ?1",
            countQuery = "SELECT count(n) FROM News as n WHERE creator.id = ?1")
    fun findByCreatorId(creatorId: Int, pageable: Pageable): Page<News>
}