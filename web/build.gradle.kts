plugins {
    kotlin("js") version "1.6.20"
}

group = "me.damon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    testImplementation(kotlin("test"))

    //testImplementation("org.hamcrest:hamcrest:2.2")

    // aggregate junit import
    //testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")

    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
}

/*
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.eclipse.jetty:jetty-servlet:11.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.eclipse.jetty:jetty-server:11.0.0")
}
 */

tasks.withType<Test> {
    useJUnitPlatform()
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
//}

kotlin {
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}