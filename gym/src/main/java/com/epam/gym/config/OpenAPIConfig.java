package com.epam.gym.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Gym",
                        email = "sanjarjon_niyazov@epam.com",
                        url = "http://"
                ),
                description = "This API exposes endpoints to manage gym application.",
                title = "Gym Management API",
                version = "1.0",
                license = @License(
                        name = "No License",
                        url = ""

                ),
                termsOfService = "QA team"
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @io.swagger.v3.oas.annotations.servers.Server(
                        description = "PROD ENV",
                        url = "no prod env yet"
                )
        }

)

public class OpenAPIConfig {
}