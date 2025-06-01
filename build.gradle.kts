import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask

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
        clion("252.18003.25")
        bundledPlugins("com.intellij.clion", "com.intellij.modules.json", "org.jetbrains.plugins.textmate")
        plugins("PythonCore:252.18003.27")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    version = scmVersion.version
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252"
            untilBuild = provider { null }
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
        failureLevel = listOf(VerifyPluginTask.FailureLevel.COMPATIBILITY_PROBLEMS)
        ides {
            ide(IntelliJPlatformType.CLion, "252.18003.25")
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
