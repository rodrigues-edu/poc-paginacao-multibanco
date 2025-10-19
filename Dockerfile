# ============================
# 🧩 Etapa 1: Build com Maven
# ============================
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Define encoding padrão para UTF-8
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

# Copia apenas o POM primeiro (melhor cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código-fonte
COPY src ./src

# Compila o projeto, ignorando testes
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# ==============================
# ☕ Etapa 2: Imagem Final (JDK)
# ==============================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o jar da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Variáveis de ambiente opcionais
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Ponto de entrada da aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
