package de.tforneberg.patchdb.controller

import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.model.Band
import de.tforneberg.patchdb.model.Patch
import de.tforneberg.patchdb.model.Patch.PatchState
import de.tforneberg.patchdb.model.Patch.PatchType
import de.tforneberg.patchdb.model.User
import de.tforneberg.patchdb.model.User.UserStatus
import de.tforneberg.patchdb.repo.PatchRepository
import de.tforneberg.patchdb.repo.UserRepository
import de.tforneberg.patchdb.repo.utils.UserUtils
import de.tforneberg.patchdb.service.imageupload.ImageUploadService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.sql.Date
import java.util.*

@RestController
@RequestMapping("/api/patches")
class PatchController(
        private val userRepository: UserRepository,
        private val patchRepository: PatchRepository,
        private val imageUploadService: ImageUploadService,
        private val userUtils: UserUtils
        ) : PatchdbController() {

    private interface PatchCompleteOthersDefaultView : Patch.CompleteView, User.DefaultView, Band.DefaultView

    //GET 
    @GetMapping(Constants.ID_MAPPING)
    @JsonView(PatchCompleteOthersDefaultView::class)
    fun getById(@PathVariable("id") id: Int, auth: Authentication?): ResponseEntity<Patch> {
        return if (hasUserAnyStatus(auth, UserStatus.admin, UserStatus.mod)) {
            getResponseOrNotFound(patchRepository.findByIdOrNull(id))
        } else {
            getResponseOrNotFound(patchRepository.findByIdAndState(id, PatchState.approved))
        }
    }

    @GetMapping("/name/{name}")
    @JsonView(Patch.DefaultView::class)
    fun getByNameApproved(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                          @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                          @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                          @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
                          @PathVariable("name") name: String
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val result = patchRepository.findByNameContainingIgnoreCaseAndState(name, PatchState.approved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping("/band/{id}")
    @JsonView(Patch.DefaultView::class)
    fun getByBandApproved(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                          @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                          @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                          @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
                          @PathVariable("id") bandId: Int
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val result = patchRepository.findByBandIdAndWithState(bandId, PatchState.approved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping("/userCreated/{id}")
    @JsonView(Patch.DefaultView::class)
    fun getByUserCreatedAndApproved(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                                    @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                                    @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                                    @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
                                    @PathVariable("id") userCreatedId: Int
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val result = patchRepository.findPatchesByCreatorIdAndWithState(userCreatedId, PatchState.approved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping("/type/{type}")
    @JsonView(Patch.DefaultView::class)
    fun getByTypeAndApproved(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                             @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                             @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                             @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
                             @PathVariable("type") type: String
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val patchType = PatchType.valueOf(type)
        val result = patchRepository.findPatchesByTypeAndWithState(patchType, PatchState.approved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping
    @JsonView(Patch.DefaultView::class)
    fun getApproved(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                    @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                    @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                    @RequestParam(name = Constants.DIRECTION, required = false) direction: String?
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val result = patchRepository.findByState(PatchState.approved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping("/state/notApproved")
    @JsonView(Patch.DefaultView::class)
    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    fun getWhereApprovalNeeded(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
                               @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                               @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                               @RequestParam(name = Constants.DIRECTION, required = false) direction: String?
    ): ResponseEntity<List<Patch>> {
        val pageable = getPageable(page, size, sortBy, direction)
        val result = patchRepository.findByState(PatchState.notApproved, pageable)
        return ResponseEntity.ok().body(result.content)
    }

    //POST
    @PostMapping
    @PreAuthorize(Constants.LOGGED_IN)
    fun addNewPatch(@RequestPart("patchData") patch: Patch,
                    @RequestPart("file") file: MultipartFile?, auth: Authentication): ResponseEntity<Patch> {
        //todo check form and file validity...
        val fileUrl = file?.let { imageUploadService.storePatchImageAndThumbnail(it) }
        return if (!fileUrl.isNullOrEmpty()) {
            patch.let {
                it.image = fileUrl
                it.userInserted = userRepository.findByName(auth.name)
                it.dateInserted = Date(Date().time)
                it.state = PatchState.notApproved
                ResponseEntity.ok().body(patchRepository.save(it))
            }
        } else {
            ResponseEntity.badRequest().build()
        }
    }

    //PATCH
    @PatchMapping(Constants.ID_MAPPING)
    @JsonView(PatchCompleteOthersDefaultView::class)
    fun updatePatch(@PathVariable("id") id: Int,
                    @RequestBody update: String,
                    auth: Authentication
    ): ResponseEntity<Patch> {
        return patchRepository.findByIdOrNull(id)?.let {
            if (isUserAllowedToDoUpdateRequest(update, Patch::class.java, auth)) {
                updateWithJsonPatch(update, it, Patch::class.java)
                ResponseEntity.ok().body(patchRepository.save(it))
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
        } ?: ResponseEntity.notFound().build()
    }

    //DELETE
    @DeleteMapping(Constants.ID_MAPPING)
    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD) //should a user be able to delete the patches that he added/created?
    fun deletePatch(@PathVariable("id") id: Int): ResponseEntity<String> {
        return patchRepository.findByIdOrNull(id)?.let {
            val imagesDeletedSuccessfully = it.image?.let {
                image -> imageUploadService.deleteImageAndThumbnailFromStorage(image)
            } ?: false
            if (imagesDeletedSuccessfully) {
                patchRepository.delete(it)
                ResponseEntity.ok().build()
            } else {
                ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Could not remove images from bucket for id $id")
            }
        } ?: ResponseEntity.notFound().build()
    }
}