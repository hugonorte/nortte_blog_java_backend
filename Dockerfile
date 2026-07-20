# Etapa 1: Build da aplicação usando a imagem oficial do Gradle com JDK 21
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copia os arquivos de configuração do Gradle
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Baixa as dependências (faz cache dessa camada)
RUN ./gradlew dependencies --no-daemon

# Copia o código fonte
COPY src/ src/

# Compila a aplicação ignorando os testes (os testes rodam no CI)
RUN ./gradlew bootJar -x test --no-daemon

# Etapa 2: Imagem final enxuta usando apenas o JRE 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# Adiciona um usuário não-root por segurança (boa prática no Cloud Run)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copia o .jar compilado da etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta 8080 (Padrão do Spring Boot e do Cloud Run)
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
