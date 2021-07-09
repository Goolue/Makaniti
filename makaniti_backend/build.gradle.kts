import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin ("jvm") version "1.4.21"
  application
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.goolue"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.0.3"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "com.goolue.makaniti.vertices.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClassName = launcherClassName
}

sourceSets {
  getByName("main").java.srcDirs("src/main/kotlin")
  getByName("test").java.srcDirs("src/test/kotlin")
}

dependencies {
  // vertx
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-auth-oauth2")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-rx-java3:4.1.0")
  implementation(kotlin("stdlib-jdk8"))
  implementation("org.slf4j", "slf4j-api", "1.7.31")
  implementation("org.slf4j", "slf4j-simple", "1.7.31")

  // aws
  implementation(platform("software.amazon.awssdk:bom:2.15.0"))
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:netty-nio-client")

  // other
  implementation("com.sksamuel.hoplite:hoplite-core:1.4.3")
  implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.3")
  implementation("com.sksamuel.hoplite:hoplite-aws:1.4.3")


  // testing
  testImplementation(platform("org.junit:junit-bom:5.7.2"))
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  implementation(kotlin("reflect"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
//  args = listOf("run", mainVerticleName)
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange", "--java-opts", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000")
//  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
