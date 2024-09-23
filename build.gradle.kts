plugins {
    kotlin("jvm") version "2.0.0"
    id("org.flywaydb.flyway") version "10.18.0"
    id("org.jooq.jooq-codegen-gradle") version "3.19.11"
}

group = "uk.co.kiteframe.habitpal"
version = "1.0-SNAPSHOT"

val jooqVersion = "3.19.11"

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
    implementation("io.arrow-kt:arrow-core:1.2.1")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-core:1.5.6")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.postgresql:postgresql:42.7.4")

    jooqCodegen("org.postgresql:postgresql:42.7.4")
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
    url = "jdbc:postgresql://localhost:5432/habitpal"
    user = "habitpal"
    password = "habitpal"
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/habitpal"
            user = "habitpal"
            password = "habitpal"
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
