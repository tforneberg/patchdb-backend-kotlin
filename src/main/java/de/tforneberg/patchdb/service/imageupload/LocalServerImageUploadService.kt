package de.tforneberg.patchdb.service.imageupload

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Service
@Profile("localDev")
class LocalServerImageUploadService : ImageUploadService {

    @Value("\${localdev.imageupload.path}")
    private val internalBasePathOnServer: String? = null

    @Value("\${localdev.imageupload.url}")
    private val baseUrlForDatabase: String? = null

    override fun uploadFile(filePathAndNameToUploadTo: String, fileToUpload: File): Boolean {
        return try {
            val pathToUploadTo = Path.of(getBasePath() + "/" + filePathAndNameToUploadTo)
            Files.createDirectories(pathToUploadTo.parent)
            Files.copy(fileToUpload.toPath(), pathToUploadTo)
            true
        } catch (e: Exception) {
            getLogger().error(e) { "error  while uploading file" }
            false
        }
    }

    override fun deleteFileFromStorage(fileName: String): Boolean {
        return try {
            //TODO check if this works
            Files.delete(Path.of(fileName))
            true
        } catch (e: Exception) {
            getLogger().error(e) { "error while deleting file" }
            false
        }
    }

    override fun getImagePathForDatabase(entityPath: String, fileName: String): String{
        return "${baseUrlForDatabase}/images/$entityPath/$fileName"
    }

    override fun getBasePath(): String {
        return internalBasePathOnServer?.plus("/images") ?: "images"
    }
}