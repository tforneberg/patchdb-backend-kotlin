package de.tforneberg.patchdb.controller

import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.error.BadRequestException
import de.tforneberg.patchdb.event.RegistrationCompleteEvent
import de.tforneberg.patchdb.event.ResetPasswordEvent
import de.tforneberg.patchdb.model.Collection
import de.tforneberg.patchdb.model.Patch
import de.tforneberg.patchdb.model.Patch.PatchState
import de.tforneberg.patchdb.model.UpdatableObject
import de.tforneberg.patchdb.model.User
import de.tforneberg.patchdb.model.User.UserStatus
import de.tforneberg.patchdb.model.dto.ChangePasswordRequestData
import de.tforneberg.patchdb.model.dto.RegisterRequestData
import de.tforneberg.patchdb.model.dto.ResetPasswordRequestData
import de.tforneberg.patchdb.repo.PatchRepository
import de.tforneberg.patchdb.repo.UserRepository
import de.tforneberg.patchdb.repo.UserVerificationTokenRepository
import de.tforneberg.patchdb.repo.utils.UserUtils
import de.tforneberg.patchdb.service.recaptcha.RecaptchaService
import de.tforneberg.patchdb.service.imageupload.ImageUploadService
import de.tforneberg.patchdb.validation.ChangePasswordRequestValidator
import de.tforneberg.patchdb.validation.RegisterRequestValidator
import de.tforneberg.patchdb.validation.ResetPasswordRequestValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/users")
class UserController(
        private val userRepository: UserRepository,
        private val userVerificationTokenRepository: UserVerificationTokenRepository,
        private val patchRepo: PatchRepository,
        private val changePwValidator: ChangePasswordRequestValidator,
        private val registerValidator: RegisterRequestValidator,
        private val resetPasswordValidator: ResetPasswordRequestValidator,
        private val passwordEncoder: PasswordEncoder,
        private val userUtils: UserUtils,
        private val eventPublisher: ApplicationEventPublisher,
        private val imageUploadService: ImageUploadService,
        private val recaptchaService: RecaptchaService
) : PatchdbController() {

    interface UserAndPatchDefaultView : Patch.DefaultView, User.DefaultView

    //GET
    @GetMapping(Constants.ID_MAPPING)
    @JsonView(User.DefaultView::class)
    fun getById(@PathVariable("id") userId: Int, auth: Authentication?): ResponseEntity<User> {
        return getResponseOrNotFound(userRepository.findByIdOrNull(userId))
    }

    @GetMapping(Constants.ID_MAPPING + "/private")
    @PreAuthorize(Constants.AUTH_ID_IS_OF_REQUESTING_USER)
    @JsonView(User.OwnerCompleteView::class)
    fun getByIdWithPrivateFields(@PathVariable("id") userId: Int): ResponseEntity<User> {
        return getResponseOrNotFound(userRepository.findByIdOrNull(userId))
    }

    @GetMapping("/login")
    fun login(@RequestParam("recaptchaToken") recaptchaToken: String): ResponseEntity<Void> {
        return if (recaptchaService.isSafe(recaptchaToken, "LOGIN")) {
            ResponseEntity.ok().build()
        } else {
            //TODO handle this in a proper way, e.g. one time password per mail
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/name/{name}") //better as query param ...  /&name={name} would allow generic search for every field, more REST conform
    @JsonView(User.CompleteView::class)
    fun getByName(@PathVariable("name") name: String): ResponseEntity<User> {
        return getResponseOrNotFound(userRepository.findByName(name))
    }

    @GetMapping
    @JsonView(User.DefaultView::class)
    fun getAll(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
               @RequestParam(name = Constants.SIZE, required = false) size: Int?,
               @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
               @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?
    ): ResponseEntity<List<User>> {
        val result = userRepository.findAll(getPageable(page, size, direction, sortBy))
        return ResponseEntity.ok().body(result.content)
    }

    @GetMapping("/registrationConfirmation")
    fun registrationConfirmation(@RequestParam("token") tokenString: String, request: HttpServletRequest): ResponseEntity<String> {
        return userVerificationTokenRepository.findByToken(tokenString)?.let { token ->
            val currentTime = Calendar.getInstance().time.time
            val tokenIsNotExpired = (token.expiryDate?.time?: -1) - currentTime > 0
            if (tokenIsNotExpired) {
                token.user?. let {
                    it.status = UserStatus.user
                    userRepository.save(it)

                    userVerificationTokenRepository.delete(token)

                    ResponseEntity.ok().body("Registration confirmed")
                }?: ResponseEntity.badRequest().body("User from token not found")
            } else {
                ResponseEntity.badRequest().body("Token expired")
            }
        } ?: ResponseEntity.badRequest().body("Token not found")
    }

    //POST
    @PostMapping("/register")
    fun register(@RequestBody req: RegisterRequestData, result: BindingResult, request: HttpServletRequest): ResponseEntity<Void> {
        registerValidator.validate(req, result)
        if (result.hasErrors()) {
            throw BadRequestException(result)
        }
        return if (recaptchaService.isSafe(req.recaptchaToken, "REGISTER")) {
            var user = User(
                name = req.name,
                email = req.email,
                status = UserStatus.unconfirmed,
                password = passwordEncoder.encode(req.password)
            )
            user = userRepository.save(user)
            eventPublisher.publishEvent(RegistrationCompleteEvent(user, request.locale))

            ResponseEntity.ok().build()
        } else {
            //TODO handle this in a proper way, e.g. one time password per mail
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/resetPassword")
    fun resetPassword(@RequestBody req: ResetPasswordRequestData, result: BindingResult, request: HttpServletRequest): ResponseEntity<Void> {
        resetPasswordValidator.validate(req, result)
        if (result.hasErrors()) {
            throw BadRequestException(result)
        }

        if (recaptchaService.isSafe(req.recaptchaToken, "RESET_PASSWORD")) {
            userRepository.findByEmail(req.email)?.let {
                eventPublisher.publishEvent(ResetPasswordEvent(it))
                return ResponseEntity.ok().build()
            }
        }
        return ResponseEntity.badRequest().build()
    }

    @PostMapping(Constants.ID_MAPPING + "/image")
    @PreAuthorize(Constants.AUTH_ID_IS_OF_REQUESTING_USER)
    fun uploadImage(@PathVariable("id") userId: Int, file: MultipartFile?): ResponseEntity<Void> {
        return userRepository.findByIdOrNull(userId)?.let { user ->
            val fileUrl = file?.let { file -> imageUploadService.storeUserImageAndThumbnail(file) }
            return if (!fileUrl.isNullOrEmpty()) {
                user.image = fileUrl
                userRepository.save(user)
                ResponseEntity.ok().build()
            } else {
                ResponseEntity.badRequest().build()
            }
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping(Constants.ID_MAPPING + "/patches")
    @JsonView(UserAndPatchDefaultView::class)
    fun getUserPatches(@PathVariable("id") id: Int,
                       @RequestParam(name = Constants.PAGE, required = false) page: Int?,
                       @RequestParam(name = Constants.SIZE, required = false) size: Int?,
                       @RequestParam(name = Constants.DIRECTION, required = false) direction: String?,
                       @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
                       auth: Authentication?): ResponseEntity<Collection> {
        val pageable = getPageable(page, size, sortBy, direction)
        return userRepository.findByIdOrNull(id)?.let {
            val patches = if (hasUserAnyStatus(auth, UserStatus.admin, UserStatus.mod)) {
                patchRepo.findPatchesByUserId(id, pageable)
            } else {
                patchRepo.findPatchesByUserIdAndWithState(id, PatchState.approved.name, pageable)
            }
            ResponseEntity.ok(Collection(patches.content, it.name))
        } ?: ResponseEntity.notFound().build()
    }

    //PATCH
    @PatchMapping(Constants.ID_MAPPING)
    @JsonView(User.CompleteView::class)
    fun updateUser(@PathVariable("id") id: Int,
                   @RequestBody update: String,
                   auth: Authentication?
    ): ResponseEntity<User> {
        return userRepository.findByIdOrNull(id)?.let {
            if (isUserAllowedToDoUpdateRequest(update, User::class.java, auth)) {
                updateWithJsonPatch(update, it, User::class.java)
                ResponseEntity.ok().body(userRepository.save(it))
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            }
        } ?: ResponseEntity.notFound().build()
    }

    @PatchMapping(Constants.ID_MAPPING + "/password")
    @PreAuthorize(Constants.AUTH_ID_IS_OF_REQUESTING_USER) //password change by already logged in user
    fun changePassword(@RequestBody data: ChangePasswordRequestData,
                       @PathVariable("id") userId: Int,
                       result: BindingResult
    ): ResponseEntity<String> {
        changePwValidator.validate(data, result)
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("validation error: $result")
        }
        return userRepository.findByIdOrNull(userId)?.let { user ->
            user.password = passwordEncoder.encode(data.password)
            userRepository.save(user)
            ResponseEntity<String>(HttpStatus.OK)
        } ?: ResponseEntity<String>(HttpStatus.NOT_FOUND)
    }

    @PatchMapping(Constants.ID_MAPPING + "/patches")
    @PreAuthorize(Constants.AUTH_ID_IS_OF_REQUESTING_USER)
    fun addOrRemovePatch(@RequestBody data: Patch, @PathVariable("id") userId: Int): ResponseEntity<String> {
        data.id?.let { patchId ->
            when (data.operation) {
                UpdatableObject.HttpPatchOperation.add -> userRepository.insertIntoCollection(patchId, userId)
                UpdatableObject.HttpPatchOperation.remove -> userRepository.deletePatchFromCollection(patchId, userId)
                else -> return ResponseEntity.badRequest().body("${data.operation} operation not supported")
            }
            return ResponseEntity.ok().build()
        } ?: return ResponseEntity.badRequest().body("id is missing in request body")
    }
}