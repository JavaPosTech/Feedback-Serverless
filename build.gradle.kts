plugins {
    java
    id("io.quarkus") version "3.40.1"
}

version = "1.0.0"
group = "br.com.fiap"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

configurations.configureEach {
    exclude(group = "com.azure", module = "azure-core-http-netty")
    resolutionStrategy.force("org.projectlombok:lombok:1.18.48")
}

dependencies {

    // Quarkus
    implementation("io.quarkus:quarkus-qute")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-jackson")
    implementation("io.quarkus:quarkus-azure-functions")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-hibernate-orm-panache")
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.40.1"))

    // Azure
    implementation("com.azure:azure-identity")
    implementation("com.azure:azure-storage-queue")
    implementation("com.azure:azure-communication-email")
    implementation(platform("com.azure:azure-sdk-bom:1.3.9"))
    implementation("com.azure:azure-core-http-jdk-httpclient")
    compileOnly("com.microsoft.azure.functions:azure-functions-java-library")
    testImplementation("com.microsoft.azure.functions:azure-functions-java-library")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Banco de dados
    implementation("io.quarkus:quarkus-jdbc-mssql")
    implementation("io.quarkus:quarkus-flyway-mssql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")

    // Testes
    testImplementation("io.quarkus:quarkus-junit")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}