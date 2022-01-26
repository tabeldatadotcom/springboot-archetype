package com.maryanto.dimas.archetype.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket apiDocket(
            @Value("${spring.application.name}") String applicationName,
            @Value("${spring.application.version}") String applicationVersion,
            @Value("${spring.application.fullname}") String applicationFullName,
            @Value("${application.developer.name}") String developerName,
            @Value("${application.developer.email}") String developerEmail,
            @Value("${application.developer.organisation-url}") String companyUrl,
            @Value("${application.license.name}") String licenseName,
            @Value("${application.license.url}") String licenseUrl) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .build()
                .apiInfo(new ApiInfo(
                        applicationName,
                        applicationFullName,
                        applicationVersion,
                        "TERMS OF SERVICE URL",
                        new Contact(developerName, companyUrl, developerEmail),
                        licenseName,
                        licenseUrl,
                        new ArrayList<>()
                ));
    }

}
