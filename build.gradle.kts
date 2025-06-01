import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.6.0"
    id("pl.allegro.tech.build.axion-release") version "1.18.16"
}

group = "pro.jothe"
version = scmVersion.version

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        clion("2025.1")
        bundledPlugins("com.intellij.clion", "PythonCore", "com.intellij.modules.json", "org.jetbrains.plugins.textmate")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    version = scmVersion.version
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
            untilBuild = "251.*"
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = providers.environmentVariable("PUBLISH_CHANNELS").map { it.split(',') }
    }

    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.CLion)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "243"
                untilBuild = "251.*"
            }
        }
    }
}

tasks {
    prepareSandbox {
        from(layout.projectDirectory.dir("bundles")) {
            into(pluginName.map { "$it/lib/bundles" })
        }
    }
}
