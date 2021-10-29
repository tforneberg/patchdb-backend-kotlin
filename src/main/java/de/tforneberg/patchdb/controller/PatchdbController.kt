package de.tforneberg.patchdb.controller

import com.fasterxml.jackson.databind.ObjectMapper
import de.tforneberg.patchdb.model.User.UserStatus
import de.tforneberg.patchdb.security.ChangeAllowedBy
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.util.ReflectionUtils
import org.springframework.util.StringUtils
import java.io.IOException
import java.lang.reflect.Field
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.asStream

abstract class PatchdbController {

    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()

    /**
     * Updates a given object (objectToUpdate) of given type (type) with the given string (string).
     * Therefore, the string (containing a JSON stringified part of the object to update, containing only the fields that should be updated)
     * gets converted into a object of the given type. After this, the fields of the given object get set with the fields from the
     * parsed object. This method also checks the Authentication of the given user to update the fields, first.
     * The fields need a HttpPatchAllowed annotation containing the role of auth
     *
     * @param string the string containing a JSON Patch (a part of the object to update, containing only the fields with new values)
     * @param objectToUpdate the object to update
     * @param type the type of the object to update
     * @return true if the operation is successful, false otherwise
     */
	fun <T> updateWithJsonPatch(string: String, objectToUpdate: T, type: Class<T>): Boolean {
        return try {
            val objectWithOnlyUpdatedFields = objectMapper.readValue(string, type)
            for (field in getFieldsFromJSONRequest(string, type)) {
                val fieldFirstLetterUppercase = StringUtils.capitalize(field.name)
                val setterForField = ReflectionUtils.findMethod(type, "set$fieldFirstLetterUppercase", field.type)
                val getterForField = ReflectionUtils.findMethod(type, "get$fieldFirstLetterUppercase")
                if (setterForField != null && getterForField != null) {
                    setterForField.invoke(objectToUpdate, getterForField.invoke(objectWithOnlyUpdatedFields))
                }
            }
            true
        } catch (e: Exception) {
            logger.error(e.message, e)
            false
        }
    }

	fun <T> isUserAllowedToDoUpdateRequest(request: String, type: Class<T>, auth: Authentication?): Boolean {
        return try {
            getFieldsFromJSONRequest(request, type).stream()
                    .allMatch { field: Field -> isUserAllowedToDoUpdateOnField(auth, field) }
        } catch (e: IOException) {
            logger.error(e.message, e)
            false
        }
    }

    @Throws(IOException::class)
    private fun <T> getFieldsFromJSONRequest(string: String, type: Class<T>): MutableList<Field> {
        return objectMapper.readTree(string).fieldNames().asSequence().asStream()
                .map { fieldName: String -> ReflectionUtils.findField(type, fieldName) }
                .filter { obj: Field? -> Objects.nonNull(obj) }.collect(Collectors.toList())
    }

    private fun isUserAllowedToDoUpdateOnField(auth: Authentication?, field: Field): Boolean {
        val annotation = field.getAnnotation(ChangeAllowedBy::class.java)
        return annotation != null && Arrays.stream(annotation.roles)
                .anyMatch { role: UserStatus -> userHasStatus(auth, role) }
    }

    /**
     * Checks if the given user/authentication has the given UserStatus in his authority string
     */
    fun userHasStatus(auth: Authentication?, userStatus: UserStatus): Boolean {
        return auth != null && auth.authorities.stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .anyMatch { authString: String -> authString == userStatus.toString() }
    }

    /**
     * Checks if the given user/authentication has any of the given UserStatus in his authority string
     */
	fun hasUserAnyStatus(auth: Authentication?, vararg userStatusArray: UserStatus?): Boolean {
        return auth != null && auth.authorities.stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .anyMatch { authString: String -> userStatusArray.asSequence().asStream()
                            .map { userStatus -> userStatus.toString() }
                            .collect(Collectors.toList())
                            .contains(authString)
                }
    }

	fun getPageable(page: Int?, size: Int?, propertyToSortBy: String?, sortDirection: String?): Pageable {
        val sortingProperties: Sort = if (!propertyToSortBy.isNullOrEmpty()) {
            Sort.by(Sort.Direction.fromString(sortDirection?:"desc"), propertyToSortBy)
        } else {
            Sort.unsorted()
        }
        return PageRequest.of(page ?: 0, size ?: Int.MAX_VALUE, sortingProperties)
    }

	fun <T> getResponseOrNotFound(result: T?): ResponseEntity<T> {
        return result?.let { ResponseEntity.ok().body(it) }?: ResponseEntity.notFound().build();
    }

    fun <T> getResponseOrBadRequest(result: T?): ResponseEntity<T> {
        return result?.let { ResponseEntity.ok().body(it) }?: ResponseEntity.badRequest().build();
    }
}