plugins {
    java
    groovy
    idea
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.2" // jib
}

group = "com.klipwallet"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

jib {
    from {
        image = "amazoncorretto:17-alpine-jdk"
    }
    to {
        image = System.getenv("ECR_REGISTRY") + "/" + System.getenv("ECR_REPOSITORY")
        tags = setOf(System.getenv("IMAGE_TAG") ?: "default_tag")
    }
    container {
        
        jvmFlags = listOf(
            System.getenv("JVM_XMS") ?: "-Xms256m", 
            System.getenv("JVM_XMX") ?: "-Xmx256m",

        )
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    configureEach {
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("org.apache.logging.log4j", "log4j-to-slf4j")
        exclude("log4j", "log4j")
    }
}

repositories {
    maven("https://maven-central-asia.storage-download.googleapis.com/maven2/") // Proxy of maven central
    mavenCentral()
}

// Properties
extra["springCloudVersion"] = "2022.0.3"
extra["springdocVersion"] = "2.2.0"
extra["spockVersion"] = "2.3-groovy-4.0"
extra["newrelicLogbackVersion"] = "2.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.lettuce:lettuce-core")    // Redis
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springdocVersion")}")
    // Newrelic
    implementation("com.newrelic.logging:logback:${property("newrelicLogbackVersion")}")

//    implementation("com.newrelic.agent.java:newrelic-api:7.6.0")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")    // Fix https://stackoverflow.com/questions/53326271/spring-nullable-annotation-generates-unknown-enum-constant-warning

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("com.h2database:h2")
//    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.micrometer:micrometer-registry-new-relic")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("com.querydsl:querydsl-apt::jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")        // For querydsl-apt::jakarta
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")      // For querydsl-apt::jakarta

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:vault")
    testImplementation(platform("org.spockframework:spock-bom:${property("spockVersion")}"))
    testImplementation("org.spockframework:spock-core")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.processResources {
    filesMatching("**/application.yml") {
        expand("_applicationVersion" to project.version)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.3.3"
    maxWarnings = 0
    configFile = file("${rootDir}/config/checkstyle/gx-checkstyle.0.9.xml")
}
