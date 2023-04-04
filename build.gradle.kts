plugins {
    id("org.flywaydb.flyway") version "7.15.0"
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.tsenyurt.parser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.15.1")

    // https://mvnrepository.com/artifact/me.liuwj.ktorm/ktorm-core
    implementation("me.liuwj.ktorm:ktorm-core:3.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.1.2")

    implementation("org.flywaydb:flyway-core:7.15.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

flyway {
    url = System.getProperty("DB_URL")
    user = System.getProperty("DB_USER")
    password = System.getProperty("DB_PASS")
    locations = arrayOf("filesystem:./src/main/resources/db/migration")
}
