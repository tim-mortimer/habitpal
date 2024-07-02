plugins {
    kotlin("jvm") version "2.0.0"
}

group = "uk.co.kiteframe.habitpal"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.20.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-client-apache")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("io.arrow-kt:arrow-core:1.2.1")
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