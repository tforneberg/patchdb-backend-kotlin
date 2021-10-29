package de.tforneberg.patchdb.repo

import de.tforneberg.patchdb.model.Band
import org.springframework.data.jpa.repository.JpaRepository

interface BandRepository : JpaRepository<Band, Int>