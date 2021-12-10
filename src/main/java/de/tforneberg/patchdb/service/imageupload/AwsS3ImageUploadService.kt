package de.tforneberg.patchdb.service.imageupload

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import javax.annotation.PostConstruct

@Service
class AwsS3ImageUploadService : ImageUploadService {

    @Value("\${aws.endpointUrl}")
    private val endpointUrl: String? = null

    @Value("\${aws.bucketName}")
    private val bucketName: String? = null

    @Value("\${aws.secretKey}")
    private val secretKey: String? = null

    @Value("\${aws.accessKey}")
    private val accessKey: String? = null

    private lateinit var client: S3Client

    @PostConstruct
    private fun initialize() {
        val credentials: AwsCredentials = AwsBasicCredentials.create(accessKey, secretKey)
        client = S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()
    }

    override fun getBasePath() : String {
        return "$endpointUrl/$bucketName"
    }

    override fun deleteFileFromStorage(fileName: String): Boolean {
        val response = client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build())
        return response.sdkHttpResponse().isSuccessful
    }

    override fun uploadFile(filePathAndName: String, file: File): Boolean {
        val response = client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePathAndName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build(),
                RequestBody.fromFile(file))
        return response.sdkHttpResponse().isSuccessful
    }

}