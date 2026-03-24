FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

RUN ls -lh target

FROM eclipse-temurin:17
WORKDIR /app

COPY --from=build /build/target/product-service*.jar product-service.jar

EXPOSE 8070

CMD ["java", "-jar", "product-service.jar"]
