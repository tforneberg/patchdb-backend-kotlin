package de.tforneberg.patchdb.service.imageupload

import mu.KLogger
import mu.KotlinLogging
import net.coobird.thumbnailator.Thumbnails
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

interface ImageUploadService {

    fun uploadFile(filePathAndNameToUploadTo:String, fileToUpload:File) : Boolean

    fun deleteFileFromStorage(fileName: String): Boolean

    fun storePatchImageAndThumbnail(multipartFile: MultipartFile): String? {
        return storeImageAndThumbnail(multipartFile, "patches", 1500, 300)
    }

    fun storeUserImageAndThumbnail(multipartFile: MultipartFile): String? {
        return storeImageAndThumbnail(multipartFile, "users", 1000, 200)
    }

    fun deleteImageAndThumbnailFromStorage(imageUrl: String): Boolean {
        val fileName = getFilenameFromUrl(imageUrl)
        val bigImageDeleted = deleteFileFromStorage(fileName)
        return if (bigImageDeleted) deleteFileFromStorage(THUMBNAIL_PREFIX + fileName) else false
    }

    fun deleteFileFromStorageByUrl(fileUrl: String): Boolean {
        val fileName = getFilenameFromUrl(fileUrl)
        return deleteFileFromStorage(fileName)
    }

    fun getBasePath() : String

    fun getLogger(): KLogger {
        return KotlinLogging.logger {}
    }

    /**
     * Uploads an image to the s3 storage after extracting it from the given MultipartFile. Returns the url.
     * Also, a smaller image is uploaded (thumbnail version) with the name/url suffix "_s".
     * @param multipartFile the file to save.
     * @return the url under which the file was saved (non-thumbnail version).
     */
    fun storeImageAndThumbnail(multipartFile: MultipartFile,
                               entityPath: String,
                               imageMaxSize: Int,
                               thumbnailMaxSize: Int): String? {
        var originalFile: File? = null
        try {
            originalFile = convertMultiPartToFile(multipartFile)
            val fileName = generateTimeStampFileName(multipartFile)
            if (originalFile != null && fileName != null) {
                val fileNameSmall = THUMBNAIL_PREFIX + fileName
                val bufferedOriginalImage: BufferedImage = ImageIO.read(originalFile)
                storeImageWithMaxSize(bufferedOriginalImage, originalFile, imageMaxSize, fileName, entityPath)
                storeImageWithMaxSize(bufferedOriginalImage, originalFile, thumbnailMaxSize, fileNameSmall, entityPath)

                return getImagePathForDatabase(entityPath, fileName)
            } else {
                getLogger().error() { "could not upload image" }
            }
        } catch (e: Exception) {
            getLogger().error(e) { "error while uploading file" }
        } finally {
            deleteTemporaryFile(originalFile);
        }
        return null
    }

    private fun storeImageWithMaxSize(image: BufferedImage, imageFile: File, maxSize:Int, fileName:String, path: String) {
        var tempFileToUpload = File(fileName)
        try {
            if (image.height > maxSize || image.width > maxSize) {
                Thumbnails.of(image)
                        .size(maxSize, maxSize)
                        .toFile(tempFileToUpload)
            } else {
                tempFileToUpload = imageFile
            }
            uploadFile("$path/$fileName", tempFileToUpload)
        } catch (e: Exception) {
            getLogger().error { e }
        } finally {
            deleteTemporaryFile(tempFileToUpload);
        }
    }

    fun deleteTemporaryFile(temporaryFile: File?) {
        temporaryFile?.delete();
    }

    @Throws(IOException::class)
    private fun convertMultiPartToFile(multipartFile: MultipartFile): File? {
        return multipartFile.originalFilename?.let {
            val resultFile = File(it)
            val fileOutputStream = FileOutputStream(resultFile)
            fileOutputStream.write(multipartFile.bytes)
            fileOutputStream.close()
            resultFile
        }
    }

    private fun generateTimeStampFileName(multiPart: MultipartFile): String? {
        return multiPart.originalFilename?.let {
            val dataType = it.substring(it.lastIndexOf("."))
            Date().time.toString() + dataType
        }
    }

    fun getFilenameFromUrl(fileUrl: String): String {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1)
    }

    fun getImagePathForDatabase(entityPath: String, fileName: String): String{
        return "${getBasePath()}/$entityPath/$fileName"
    }

    companion object {
        const val THUMBNAIL_PREFIX = "s_"
    }
}