# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
LABEL authors="leonardomuniz"
WORKDIR /app
# Copia apenas o pom.xml primeiro para aproveitar o cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e gera o .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Imagem leve)
FROM eclipse-temurin:21-jre-alpine
LABEL authors="leonardomuniz"
WORKDIR /app
# Copia o jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]