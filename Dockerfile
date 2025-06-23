# Dockerfile

# Etapa 1: Construcción con Maven
# Usamos una imagen de Maven con Java 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos solo el pom.xml para descargar dependencias eficientemente
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiamos el resto del código fuente y construimos el JAR
COPY src ./src
RUN mvn package -DskipTests

# Etapa 2: Ejecución
# Usamos una imagen más ligera solo con Java para correr la aplicación
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos el JAR construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto 8080 para acceder a la aplicación
EXPOSE 8080
# Comando para iniciar la aplicación al levantar el contenedor
ENTRYPOINT ["java","-jar","app.jar"]