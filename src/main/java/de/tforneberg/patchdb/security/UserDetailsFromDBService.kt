package de.tforneberg.patchdb.security

import de.tforneberg.patchdb.repo.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsFromDBService(private val userRepo:UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String): UserDetails? {
        return userRepo.findByName(userName)?.let { user ->
            //Get the users' authority
            val authority: GrantedAuthority = SimpleGrantedAuthority(user.status.toString())

            //Construct and return a UserDetails implementation object
            return org.springframework.security.core.userdetails.User(user.name, user.password, listOf(authority))
        }
    }
}