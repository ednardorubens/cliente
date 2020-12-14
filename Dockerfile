FROM openjdk:14-jdk-alpine
COPY target/cliente.jar cliente.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /cliente.jar ${0} ${@}"]