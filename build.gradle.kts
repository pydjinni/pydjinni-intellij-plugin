import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.3.0"
    id("pl.allegro.tech.build.axion-release") version "1.18.16"
}

group = "pro.jothe"
version = scmVersion.version

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        clion("2024.3.1")
        bundledPlugins(listOf("PythonCore", "org.jetbrains.plugins.textmate"))
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
        ides {
            recommended()
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
