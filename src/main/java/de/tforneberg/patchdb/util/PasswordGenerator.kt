package de.tforneberg.patchdb.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

object PasswordGenerator {
    @JvmStatic
    fun main(args: Array<String>) {
        val encoder = BCryptPasswordEncoder()
        if (args.isNotEmpty()) {
            println(encoder.encode(args[0]))
        } else {
            // create a scanner so we can read the command-line input
            val scanner = Scanner(System.`in`)

            //  prompt for the user's name
            print("Enter password: ")

            // get their input as a String
            val password = scanner.next()
            println(encoder.encode(password))
            scanner.close()
        }
    }
}