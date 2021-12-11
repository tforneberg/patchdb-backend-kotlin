package de.tforneberg.patchdb.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
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
class SecurityConfig(
        private val userDetailsService:UserDetailsFromDBService,
        private val environment:Environment
        ) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        //Allow CORS from the front-end domain
        val config = CorsConfiguration()
        val allowedOrigins = environment.getProperty("cors.allowedUrls")
        if (allowedOrigins != null) {
            config.allowedOrigins = listOf(*allowedOrigins.split(",").toTypedArray()) //allowed front-end URLs
        }
        config.addAllowedHeader("*") //any header
        config.addAllowedMethod("*") //any method
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    override fun configure(http: HttpSecurity) {
        configureSession(http)
    }

    private fun configureSession(http: HttpSecurity) {
        val oneMonthInSeconds = 2629746

        http
                .httpBasic()
            .and()
                .rememberMe().rememberMeParameter("remember").tokenValiditySeconds(oneMonthInSeconds)
            .and()
                .authorizeRequests()
                    //.antMatchers("/", "/js/**", "/css/**", "/img/**", "/favicon.ico", "/index.html").permitAll() //everyone should be able to retrieve frontend
                    .antMatchers("/api/users/register").permitAll() //everyone should be able to register
                    .antMatchers("/api/users/registrationConfirmation").permitAll() //everyone should be able to confirm registration
                    .antMatchers("/api/patches").permitAll() //everyone should be able to get patches list
                    .antMatchers("/api/**").authenticated() //everything else from API requires authentication
            .and()
                //Configure CSRF prevention/security, tell Spring to send XSRF-Token in Cookie
                //(Clients can obtain the XSRF-Token by calling e.g. GET "/")
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
                //Add the CORS filter applied in corsConfigurationSource()
                .cors()
            .and()
                .logout() //Configure logout behavior
                    .logoutUrl("/api/logout") //specify logout URL
                    .logoutSuccessHandler { _, res, _ -> res.status = HttpServletResponse.SC_OK } //do not auto redirect after logout
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                //Tell auth to use the custom userDetailService that gets the user data from DB
                .userDetailsService(userDetailsService)
                //Tell auth to use BCryptPasswordEncoder
                .passwordEncoder(passwordEncoder())
    }
}