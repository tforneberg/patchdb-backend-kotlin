package de.tforneberg.patchdb.repo

import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry

@Component
class RepoConfig : RepositoryRestConfigurer {
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration, cors: CorsRegistry) {
        config.repositoryDetectionStrategy = RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED
    }
}