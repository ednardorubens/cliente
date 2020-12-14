package br.com.ermig.cliente.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {
	
	@Autowired
	private BuildProperties buildProperties;
	
    @Bean
    public Docket api() {
	    final List<ResponseMessage> responseMessages = Arrays.asList(
			response(HttpStatus.OK, "Requisição OK"),
			response(HttpStatus.CREATED, "Recurso criado"),
			response(HttpStatus.ACCEPTED, "Requisição aceita"),
			response(HttpStatus.NO_CONTENT, "Sem conteúdo"),
			response(HttpStatus.BAD_REQUEST, "Falha de requisição"),
			response(HttpStatus.NOT_FOUND, "Recurso não encontrado"),
			response(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido"),
			response(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo não suportado"),
			response(HttpStatus.UNPROCESSABLE_ENTITY, "Entidade não processada"),
			response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor"),
			response(HttpStatus.NOT_IMPLEMENTED, "Não implementado"),
			response(HttpStatus.BAD_GATEWAY, "Falha de comunicação"),
			response(HttpStatus.SERVICE_UNAVAILABLE, "Serviço não disponível")
		);
		
        return new Docket(DocumentationType.SWAGGER_2)
			.globalResponseMessage(RequestMethod.GET, responseMessages)
			.globalResponseMessage(RequestMethod.POST, responseMessages)
			.globalResponseMessage(RequestMethod.PATCH, responseMessages)
			.globalResponseMessage(RequestMethod.PUT, responseMessages)
			.globalResponseMessage(RequestMethod.DELETE, responseMessages)
			.select()
			.apis(RequestHandlerSelectors.basePackage("br.com.ermig.cliente.controller"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(metaData())
			.useDefaultResponseMessages(false);
}

	private ResponseMessage response(final HttpStatus status, final String message) {
		return new ResponseMessageBuilder()
	       .code(status.value())
	       .message(message)
	       .build();
	}
    
    private ApiInfo metaData() {
    	return new ApiInfoBuilder()
            .title(buildProperties.get("title"))
            .description(buildProperties.get("description"))
            .version(buildProperties.getVersion())
            .license(buildProperties.get("license.name"))
            .licenseUrl(buildProperties.get("license.url"))
            .build();
    }
    
}
