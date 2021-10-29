package de.tforneberg.patchdb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import javax.persistence.*

@Entity
@Table(name = "bands")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonView(Band.DefaultView::class)
data class Band(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    @field:Column(name = "id")
    var id: Int?,

    @field:Column(name = "name")
    var name: String? = null,

    @field:ElementCollection(fetch = FetchType.LAZY)
    @field:CollectionTable(name = "patches", joinColumns = [JoinColumn(name = "band_id")])
    @field:Column(name = "id")
    @field:JsonView(CompleteView::class)
    val patchIDs: Set<Int>? = null
) {
    @JsonIgnoreProperties
    interface BriefView

    @JsonIgnoreProperties
    interface DefaultView : BriefView

    @JsonIgnoreProperties
    interface CompleteView : DefaultView

    //TODO user created ?
    //TODO date ?
    //TODO refactor/rename to "Artist"
}