package com.waes.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

    public static final String TITLE = "WAES / Philips assignment - Scalable Web";
    public static final String DESCRIPTION = "REST API to maintain Products Catalogue";
    public static final String CONTACT_NAME = "Jonathan de Paula";
    public static final String CONTACT_EMAIL = "jonathanpaula22@gmail.com";
    public static final String CONTACT_URL = "https://www.linkedin.com/in/jonathan-de-paula/?locale=en_US";

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.waes.test.controller"))
                .build()
                .apiInfo(metaData())
                .useDefaultResponseMessages(false);

    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title(TITLE)
                .description(DESCRIPTION)
                .contact(new Contact(CONTACT_NAME,
                        CONTACT_URL,
                        CONTACT_EMAIL))
                .build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
