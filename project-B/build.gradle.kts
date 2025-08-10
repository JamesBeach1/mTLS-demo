plugins {
    id("java")
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.5.3")
    implementation("org.springframework.boot:spring-boot-starter:3.5.3")
    implementation("org.springframework.boot:spring-boot-actuator:3.5.3")
    implementation("org.springframework:spring-context:6.2.8")
    implementation("org.springframework:spring-web:6.2.8")


    implementation("org.apache.httpcomponents.client5:httpclient5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")
    implementation("org.slf4j:slf4j-api")
}

tasks.test {
    useJUnitPlatform()
}