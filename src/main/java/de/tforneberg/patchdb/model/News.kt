package de.tforneberg.patchdb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "news")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonView(News.DefaultView::class)
class News(
        @field:Id
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Column(name = "id")
        var id: Int?,

        @field:Column(name = "title")
        var title: String? = null,

        @field:Column(name = "content")
        var content: String? = null,

        @field:Column(name = "date_created")
        var created: Date? = null,

        @field:OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH, CascadeType.REFRESH])
        @field:JoinColumn(name = "created_by")
        var creator: User? = null
) {
    @JsonIgnoreProperties
    interface BriefView

    @JsonIgnoreProperties
    interface DefaultView : BriefView

    @JsonIgnoreProperties
    interface CompleteView : DefaultView
}