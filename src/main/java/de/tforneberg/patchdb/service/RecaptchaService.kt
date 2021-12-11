package de.tforneberg.patchdb.service

import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient
import com.google.recaptchaenterprise.v1.Assessment
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest
import com.google.recaptchaenterprise.v1.Event
import com.google.recaptchaenterprise.v1.ProjectName
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class RecaptchaService {

    @Value("\${google.recaptcha.projectid}")
    private val projectID: String? = null

    @Value("\${google.recaptcha.key.site}")
    private val recaptchaSiteKey: String? = null

    private val logger: KLogger = KotlinLogging.logger {}

    fun isAValidUserAction(token: String?, recaptchaAction: String?, validThreshold: Float = 0.3F): Boolean {
        return try {
            createAssessment(token, recaptchaAction)?.let {
                isAValidUserAction(it, recaptchaAction, validThreshold)
            } ?: true
        } catch (e: Exception) {
            logger.error(e) { "error while checking recaptcha" }
            false
        }
    }

    fun isAValidUserAction(assessment: Assessment, recaptchaAction: String?, validThreshold: Float = 0.3F): Boolean {
        // Check if the token is valid.
        if (!assessment.tokenProperties.valid) {
            logger.error { "The CreateAssessment call failed because the token was: " +
                    assessment.tokenProperties.invalidReason.name }
            return false
        }

        // Check if the expected action was executed.
        // (If the key is checkbox type and 'action' attribute wasn't set, skip this check.)
        if (!assessment.tokenProperties.action.equals(recaptchaAction)) {
            logger.error { "The action attribute in your reCAPTCHA tag " +
                        "does not match the action you are expecting to score" }
            return false
        }

        // Get the risk score and the reason(s).
        // For more information on interpreting the assessment,
        // see: https://cloud.google.com/recaptcha-enterprise/docs/interpret-assessment
        val recaptchaScore: Float = assessment.riskAnalysis.score

        logger.info { "The reCAPTCHA score is: $recaptchaScore" }

        for (reason in assessment.riskAnalysis.reasonsList) {
            logger.info { reason }
        }
        return recaptchaScore > validThreshold
    }

    /**
     * Create an assessment to analyze the risk of an UI action.
     *
     * @param token: The token obtained from the client on passing the recaptchaSiteKey.
     * @param recaptchaAction: Action name corresponding to the token.
     */
    @Throws(IOException::class)
    fun createAssessment(token: String?, recaptchaAction: String?): Assessment? {
        // Initialize a client that will be used to send requests. This client needs to be created only
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the `client.close()` method on the client to safely
        // clean up any remaining background resources.
        RecaptchaEnterpriseServiceClient.create().use { client ->
            // Set the properties of the event to be tracked.
            val event: Event = Event.newBuilder()
                .setSiteKey(recaptchaSiteKey)
                .setToken(token)
                .build()

            // Build the assessment request.
            val createAssessmentRequest: CreateAssessmentRequest = CreateAssessmentRequest.newBuilder()
                .setParent(ProjectName.of(projectID).toString())
                .setAssessment(Assessment.newBuilder().setEvent(event).build())
                .build()

            return client.createAssessment(createAssessmentRequest)
        }
    }

}