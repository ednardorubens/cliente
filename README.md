# Cliente API
API REST Java com Spring Boot de CRUD de Cliente (id, nome, cpf, dataNascimento) com GET, POST, DELETE, PATCH e PUT.

## Requisitos para rodar a aplicação
 - [JDK 14](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html)
 - [Maven](https://maven.apache.org/download.cgi)
 - [Docker](https://www.docker.com/get-started)

## Construir e rodar a API
```
$ mvn clean package dockerfile:build
$ docker-compose up
```

## Swagger
Você pode aprender e utilizar a api através da documentação do swagger na URL `http://localhost:8080/swagger-ui.html`

## Recursos
 - [Spring Boot](https://spring.io/projects/spring-boot)
 - [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service)
 - [Spring Cache](https://spring.io/guides/gs/caching)
 - [Caffeine](https://www.caffeine.tv)
 - [Spring Data Jpa](https://spring.io/guides/gs/accessing-data-jpa)
 - [Hibernate](https://hibernate.org)
 - [PostgreSQL](https://www.postgresql.org/docs/13/index.html)
 - [H2 Database](http://www.h2database.com/html/main.html)
 - [Flyway](https://flywaydb.org/getstarted/java)
 - [Lombok](https://projectlombok.org)
 - [Swagger 2](https://springfox.github.io/springfox/docs/current)
 - [Spring Boot Test](https://spring.io/guides/gs/testing-web)