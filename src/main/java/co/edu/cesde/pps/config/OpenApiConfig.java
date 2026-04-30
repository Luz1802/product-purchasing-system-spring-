package co.edu.cesde.pps.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de OpenAPI/Swagger para la aplicación.
 * Define esquema de seguridad, información de la API y otras configuraciones globales.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication. " +
                                                "Include in the Authorization header as: Bearer <token>")));
    }

    private Info apiInfo() {
        return new Info()
                .title("Product Purchasing System API")
                .description("API REST completamente documentada del Sistema de Compra de Productos. " +
                        "Proporciona funcionalidades de autenticación, catálogo de productos, carrito de compras y órdenes.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("CESDE Development Team")
                        .email("dev@cesde.edu.co")
                        .url("https://cesde.edu.co"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}

