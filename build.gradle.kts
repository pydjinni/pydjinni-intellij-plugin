import org.jetbrains.intellij.tasks.PrepareSandboxTask

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.17.2"
    id("pl.allegro.tech.build.axion-release") version "1.17.0"
}

group = "pro.jothe"
version = scmVersion.version

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("241-EAP-SNAPSHOT")
    type.set("CL") // Target IDE Platform
    plugins.set(listOf("python-ce", "org.jetbrains.plugins.textmate"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<PrepareSandboxTask> {
        from("$rootDir/bundles") {
            into("${pluginName.get()}/lib/bundles")
        }
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set((System.getenv("PUBLISH_CHANNELS")?:"").split(','))
    }
}
