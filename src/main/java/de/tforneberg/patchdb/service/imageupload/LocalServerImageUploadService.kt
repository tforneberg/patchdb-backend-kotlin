package de.tforneberg.patchdb.service.imageupload

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.io.File

@Service
@Profile("localDev")
class LocalServerImageUploadService : ImageUploadService {
    override fun uploadFile(filePathAndName: String, file: File): Boolean {
        return true;
    }

    override fun deleteTemporaryFile(temporaryFile: File?) {
        //do not delete the temporary file and therefore store it! ha!
    }

    override fun deleteFileFromStorage(fileName: String): Boolean {
        return true //TODO
    }

    override fun getBasePath(): String {
        return ""
    }
}