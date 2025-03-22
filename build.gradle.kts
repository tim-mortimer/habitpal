import com.github.gradle.node.npm.task.NpmTask
import com.github.gradle.node.npm.task.NpxTask
import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.flywaydb.flyway") version "10.18.0"
    id("org.jooq.jooq-codegen-gradle") version "3.19.11"
    id("com.github.node-gradle.node") version "7.0.2"
}

group = "uk.co.kiteframe.habitpal"
version = "1.0-SNAPSHOT"

val jooqVersion = "3.19.11"

val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/habitpal"
val localDbUrl = "jdbc:postgresql://localhost:5432/habitpal"
val testDbUrl = "jdbc:postgresql://localhost:5433/habitpal"
val dbUser = System.getenv("DB_USER") ?: "habitpal"
val dbPassword = System.getenv("DB_PASSWORD") ?: "habitpal"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.20.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-htmx")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.http4k:http4k-cloudnative")
    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.postgresql:postgresql:42.7.4")

    jooqCodegen("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jooq:jooq:${jooqVersion}")
    compileOnly("org.jooq:jooq-meta:${jooqVersion}")
    compileOnly("org.jooq:jooq-codegen:${jooqVersion}")

    testImplementation("org.http4k:http4k-client-apache")
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("io.kotest:kotest-assertions-core:5.9.0")
}

tasks.register<Zip>("buildLambdaZip") {
    from(tasks.named("compileKotlin"))
    from(tasks.named("processResources"))
    into("lib") {
        from(configurations.named("compileClasspath").get())
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.18.0")
    }
}

flyway {
    driver = "org.postgresql.Driver"
    url = dbUrl
    user = dbUser
    password = dbPassword
}

tasks.register<FlywayMigrateTask>("migrateLocalDb") {
    description = "Migrates the local development database"
    url = localDbUrl
}

tasks.register<FlywayCleanTask>("cleanLocalDb") {
    description = "Cleans the local development database"
    url = localDbUrl
    cleanDisabled = false
}

tasks.register<FlywayMigrateTask>("migrateTestDb") {
    description = "Migrates the local test database"
    url = testDbUrl
}

tasks.register<FlywayCleanTask>("cleanTestDb") {
    description = "Cleans the local test database"
    url = testDbUrl
    cleanDisabled = false
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = dbUrl
            user = dbUser
            password = dbPassword
        }

        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
            }
            target {
                packageName = "uk.co.kiteframe.habitpal.db"
            }
        }
    }
}

node {
    version = "23.9.0"
    npmVersion = "10.9.2"
    download = true
}

tasks.register<NpmTask>("installDependencies") {
    description = "Installs npm dependencies"
    workingDir = file("src/main/frontend")
    args = listOf("install")
}

tasks.register<NpxTask>("buildTailwind") {
    dependsOn("installDependencies")
    description = "Builds Tailwind CSS"
    workingDir = file("src/main/frontend")
    command = "@tailwindcss/cli"
    args = listOf("-i", "main.css", "-o", "../resources/static/main.css")
}

tasks.register("watchTailwind") {
    dependsOn("installDependencies")
    description = "Runs Tailwind CSS in watch mode using Gradle's Node.js"

    doLast {
        val nodeBinDir = node.npmWorkDir.dir("npm-v${node.npmVersion.get()}/bin").get()
        val npxExecutable = nodeBinDir.file("npx").asFile.absolutePath
        ProcessBuilder(
            npxExecutable,
            "@tailwindcss/cli",
            "-i",
            "main.css",
            "-o",
            "../resources/static/main.css",
            "--watch=always"
        )
            .directory(File("src/main/frontend"))
            .inheritIO()
            .start()
            .waitFor()
    }
}

tasks.named("processResources") {
    dependsOn("buildTailwind")
}
