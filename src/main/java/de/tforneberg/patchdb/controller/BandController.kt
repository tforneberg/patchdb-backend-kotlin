package de.tforneberg.patchdb.controller

import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.model.Band
import de.tforneberg.patchdb.repo.BandRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bands")
class BandController(private val repo:BandRepository) : PatchdbController() {

    @GetMapping(Constants.ID_MAPPING)
    @JsonView(Band.CompleteView::class)
    fun getById(@PathVariable("id") id: Int): ResponseEntity<Band> {
        return getResponseOrNotFound(repo.findByIdOrNull(id))
    }

    @GetMapping
    @JsonView(Band.DefaultView::class)
    fun getAll() : ResponseEntity<List<Band>> {
        return ResponseEntity.ok().body(repo.findAll())
    }
}