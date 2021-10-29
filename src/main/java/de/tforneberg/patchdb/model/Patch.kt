package de.tforneberg.patchdb.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.security.ChangeAllowedBy
import org.hibernate.annotations.ColumnTransformer
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "patches")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonView(Patch.DefaultView::class)
class Patch(
        @field:Column(name = "id")
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Id
        var id: Int?,

        @field:Column(name = "name")
        var name: String?,

        @field:Column(name = "date_inserted")
        var dateInserted: Date?,

        @field:JsonView(CompleteView::class)
        @field:JoinColumn(name = "user_inserted")
        @field:OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH, CascadeType.REFRESH])
        var userInserted: User?,

        //TODO:
        //User[] usersChanged;
        //String[] datesChanged;
        //evtl Klasse PatchChange (extends Change?)... dann PatchChange[] changes
        @field:ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH, CascadeType.REFRESH])
        @field:JoinColumn(name = "band_id")
        @field:JsonView(CompleteView::class)
        var band: Band? = null,

        @field:Column(name = "description")
        @field:JsonView(CompleteView::class)
        var description: String? = null,

        @field:Column(name = "image")
        var image: String? = null,

        @field:Column(name = "type")
        @field:Enumerated(EnumType.STRING)
        var type: PatchType? = null,

        @field:Column(name = "state")
        @field:Enumerated(EnumType.STRING)
        @field:ChangeAllowedBy(roles = [User.UserStatus.mod, User.UserStatus.admin])
        var state: PatchState? = null,

        @field:Column(name = "num_of_copies")
        @field:JsonView(CompleteView::class)
        var numOfCopies: Int? = null,

        @field:Column(name = "release_date")
        @field:JsonView(CompleteView::class)
        var releaseDate: Date? = null,

        @field:Column(name = "manufacturer")
        @field:JsonView(CompleteView::class)
        var manufacturer: String? = null,

        @field:ManyToMany(fetch = FetchType.LAZY)
        @field:JoinTable(name = "collections",
                joinColumns = [JoinColumn(name = "patch_id")],
                inverseJoinColumns = [JoinColumn(name = "user_id")])
        @field:JsonView(CompleteView::class)
        var users: MutableList<User>? = null,

        //does not work at the moment (NullPointer) with native queries used in repository (bc of collection table) .. see https://hibernate.atlassian.net/browse/HHH-7525
        //@field:Formula("(SELECT COUNT(*) FROM collections WHERE collections.patch_id = id)")
        //Integer amountUsers;
        //workaround for Hibernate bug, see comment above
        @field:Column(name = "amount_users", insertable = false, updatable = false)
        @field:ColumnTransformer(read = "(SELECT COUNT(*) FROM collections WHERE collections.patch_id = id)")
        var amountUsers: Int? = null

) : UpdatableObject() {

    @JsonIgnoreProperties
    interface BriefView

    @JsonIgnoreProperties
    interface DefaultView : BriefView

    @JsonIgnoreProperties
    interface CompleteView : DefaultView

    enum class PatchType {
        Woven, Stitched, Printed
    }

    enum class PatchState {
        approved, notApproved
    }

    fun addUser(user: User) {
        users?.add(user) ?: run { users = mutableListOf(user) }
    }
}