package de.tforneberg.patchdb.controller

import de.tforneberg.patchdb.service.imageupload.LocalServerImageUploadService
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import software.amazon.awssdk.utils.IoUtils
import java.io.FileInputStream
import java.io.IOException

@RestController
@RequestMapping("/api/images")
@Profile("localDev")
class LocalDevImageController(private val localServerImageUploadService: LocalServerImageUploadService) {

    @GetMapping(path = ["/{entity}/{imageName}"], produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    @Throws(IOException::class)
    fun getImage(@PathVariable("entity") entity: String, @PathVariable("imageName") imageName: String): ByteArray? {
        return try {
            IoUtils.toByteArray(FileInputStream("${localServerImageUploadService.getBasePath()}/$entity/$imageName"))
        } catch (e: Exception) {
            KotlinLogging.logger {}.error(e) { "Could not get image" }
            null
        }
    }
}