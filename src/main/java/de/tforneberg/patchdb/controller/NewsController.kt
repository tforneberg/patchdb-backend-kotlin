package de.tforneberg.patchdb.controller

import com.fasterxml.jackson.annotation.JsonView
import de.tforneberg.patchdb.model.News
import de.tforneberg.patchdb.model.User
import de.tforneberg.patchdb.model.User.UserStatus
import de.tforneberg.patchdb.repo.NewsRepository
import de.tforneberg.patchdb.repo.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/news")
class NewsController(private val userRepo:UserRepository, private val newsRepo:NewsRepository) : PatchdbController() {

    interface NewsAndUserDefaultView : News.DefaultView, User.DefaultView

    //GET
    @GetMapping(Constants.ID_MAPPING)
    @JsonView(NewsAndUserDefaultView::class)
    fun getById(@PathVariable("id") id: Int): ResponseEntity<News> {
        return getResponseOrNotFound(newsRepo.findByIdOrNull(id))
    }

    @GetMapping
    @JsonView(NewsAndUserDefaultView::class)
    fun getAll(@RequestParam(name = Constants.PAGE, required = false) page: Int?,
               @RequestParam(name = Constants.SIZE, required = false) size: Int?,
               @RequestParam(name = Constants.SORTBY, required = false) sortBy: String?,
               @RequestParam(name = Constants.DIRECTION, required = false) direction: String?
    ): ResponseEntity<List<News>> {
        val news =  newsRepo.findAll(getPageable(page, size, sortBy, direction))
        return ResponseEntity.ok(news.content)
    }

    //POST
    @PostMapping
    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    fun createNews(@RequestBody news: News, result: BindingResult?): ResponseEntity<Void> {
        //TODO validate? 
        newsRepo.save(news)
        return ResponseEntity.ok().build()
    }

    //PUT
    @PutMapping(Constants.ID_MAPPING)
    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    fun updateNews(@PathVariable("id") id: Int, update: String, auth: Authentication): ResponseEntity<News> {
        return newsRepo.findByIdOrNull(id)?.let {
                    val isUserAllowedToDoUpdateRequest = isUserAllowedToDoUpdateRequest(update, News::class.java, auth)
                    if (wasCreatedByRequestingUser(it, auth) && isUserAllowedToDoUpdateRequest) {
                        updateWithJsonPatch(update, it, News::class.java)
                        ResponseEntity.ok().body(newsRepo.save(it))
                    } else {
                        ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    }
                } ?: ResponseEntity.notFound().build()
    }

    //DELETE
    @DeleteMapping(Constants.ID_MAPPING)
    @PreAuthorize(Constants.AUTH_ADMIN_OR_MOD)
    fun deleteNews(@PathVariable("id") id: Int, auth: Authentication): ResponseEntity<Void> {
        return newsRepo.findByIdOrNull(id) ?. let {
                    if (wasCreatedByRequestingUser(it, auth) || userHasStatus(auth, UserStatus.admin)) {
                        newsRepo.delete(it)
                        ResponseEntity.ok().build()
                    } else {
                        ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    }
                } ?: ResponseEntity.notFound().build()
    }

    private fun wasCreatedByRequestingUser(news: News, authentication: Authentication): Boolean {
        return news.creator?.name?.let { it == authentication.name } ?: false
    }
}