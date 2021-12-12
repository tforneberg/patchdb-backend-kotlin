package de.tforneberg.patchdb.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(private val userDetailsService:UserDetailsService) : WebSecurityConfigurerAdapter() {

    @Value("\${login.token.validity.seconds:2629746}")
    private val tokenValiditySeconds: String? = null //default is one month

    @Value("\${cors.allowedUrls}")
    private val allowedOrigins: String? = null

    @Bean
    fun bcryptPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        allowedOrigins?.let {
            corsConfiguration.allowedOrigins = it.split(",") //allowed front-end URLs
        }
        corsConfiguration.addAllowedHeader("*") //any header
        corsConfiguration.addAllowedMethod("*") //any method

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsService) //Tell auth to use the custom userDetailService that gets the user data from DB
            .passwordEncoder(bcryptPasswordEncoder()) //Tell auth to use BCryptPasswordEncoder
    }

    override fun configure(http: HttpSecurity) {
        http
            .httpBasic()
            .and()
                .rememberMe().rememberMeParameter("remember").tokenValiditySeconds(Integer.valueOf(tokenValiditySeconds))
            .and()
                .authorizeRequests()
                    .antMatchers(*getEndpointsThatDoNotRequireAuthentication()).permitAll() //everyone should be able to call these endpoints
                    .antMatchers("/api/**").authenticated() //everything else from API requires authentication
            .and()
                //Configures CSRF prevention/security, tell Spring to send XSRF-Token in Cookie
                //(Clients can obtain the XSRF-Token by calling e.g. GET "/")
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                .cors() //Adds the CORS filter applied in corsConfigurationSource()
            .and()
                .logout() //Configures logout behavior
                    .logoutUrl("/api/logout") //specify logout URL
                    .logoutSuccessHandler { _, res, _ -> res.status = HttpServletResponse.SC_OK } //do not auto redirect after logout
    }

    private fun getEndpointsThatDoNotRequireAuthentication(): Array<String> {
        return arrayOf(
            // "/", "/js/**", "/css/**", "/img/**", "/favicon.ico", "/index.html", //everyone should be able to retrieve frontend
            "/api/users/register", //everyone should be able to register
            "/api/users/registrationConfirmation", //everyone should be able to confirm registration (with valid token)
            "/api/users/resetPassword", //everyone should be able to reset password
            "/api/users/resetPasswordConfirmation", //everyone should be able to confirm password reset (with valid token)
            "/api/patches" //everyone should be able to get patches list
        )
    }
}