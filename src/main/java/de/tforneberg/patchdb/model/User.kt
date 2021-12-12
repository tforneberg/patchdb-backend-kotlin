package de.tforneberg.patchdb.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.security.ChangeAllowedBy
import javax.persistence.*

@Entity
@Table(name = "users")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
@JsonView(User.DefaultView::class)
class User(
        @field:Column(name = "id")
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Id
        var id: Int? = null,

        @field:Column(name = "name")
        var name: String?,

        @field:ChangeAllowedBy(roles = [UserStatus.mod, UserStatus.admin])
        @field:Enumerated(EnumType.STRING)
        @field:Column(name = "status")
        var status: UserStatus?,

        @field:Column(name = "email")
        @field:JsonView(OwnerCompleteView::class)
        var email: String?,

        @Column(name = "image")
        var image: String? = null,

        @field:Column(name = "password")
        @field:JsonIgnore
        var password: String? = null,

        @field:ManyToMany(fetch = FetchType.LAZY)
        @field:JoinTable(name = "collections",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "patch_id")])
        @field:JsonIgnore
        var patches: MutableList<Patch>? = null,

        @field:ElementCollection(fetch = FetchType.LAZY)
        @field:CollectionTable(name = "collections", joinColumns = [JoinColumn(name = "user_id")])
        @field:Column(name = "patch_id")
        @field:JsonView(CompleteView::class)
        var patchIDs: List<Int>? = null
) {

    @JsonIgnoreProperties
    interface BriefView

    @JsonIgnoreProperties
    interface DefaultView : BriefView

    @JsonIgnoreProperties
    interface CompleteView : DefaultView

    @JsonIgnoreProperties
    interface OwnerCompleteView : CompleteView

    enum class UserStatus {
        admin, mod, user, blockedUser, unconfirmed
    }

    fun addPatch(patch: Patch) {
        patches?.add(patch) ?:run { patches = mutableListOf(patch) }
    }

    fun removePatch(patch: Patch) {
        patches?.remove(patch)
    }
}