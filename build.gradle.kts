/*
 * SkySkipped - Hypixel Skyblock QOL mod
 * Copyright (C) 2023  Cephetir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import dev.architectury.pack200.java.Pack200Adapter
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("gg.essential.loom") version "0.10.0.+"
    id("io.github.juuxel.loom-quiltflower-mini") version "7d04f32023"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    java
    idea
}

version = "3.5"
group = "me.cephetir"

base {
    archivesName.set("SkySkipped")
}

loom {
    silentMojangMappingsLicense()
    launchConfigs {
        getByName("client") {
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            property("legacy.debugClassLoading", "true")
            property("legacy.debugClassLoadingSave", "true")
            property("legacy.debugClassLoadingFiner", "true")
            arg("--tweakClass", "me.cephetir.bladecore.loader.BladeCoreTweaker")
            arg("--mixin", "mixins.sm.json")
        }
    }
    runConfigs {
        getByName("client") {
            isIdeConfigGenerated = true
        }
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(Pack200Adapter())
        mixinConfig("mixins.sm.json")
    }
    mixin {
        defaultRefmapName.set("mixins.sm.refmap.json")
    }
}

val include: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.ilarea.ru/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    include("me.cephetir:bladecore-loader-1.8.9-forge:1.2")
    implementation("me.cephetir:bladecore-1.8.9-forge:0.0.2-c")

    implementation("net.dv8tion:JDA:5.0.0-alpha.19") {
        exclude(module = "opus-java")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

sourceSets {
    main {
        output.setResourcesDir(file("${buildDir}/classes/kotlin/main"))
    }
}

tasks {
    wrapper.get().doFirst {
        delete("$buildDir/libs/")
    }
    processResources {
        inputs.property("version", project.version)
        inputs.property("mcversion", "1.8.9")

        filesMatching("mcmod.info") {
            expand(mapOf("version" to project.version, "mcversion" to "1.8.9"))
        }
        dependsOn(compileJava)
    }
    jar {
        manifest {
            attributes(
                mapOf(
                    "ForceLoadAsMod" to true,
                    "ModSide" to "CLIENT",
                    "ModType" to "FML",
                    "TweakClass" to "me.cephetir.bladecore.loader.BladeCoreTweaker",
                    "TweakOrder" to "0",
                    "MixinConfigs" to "mixins.sm.json"
                )
            )
        }
        dependsOn(shadowJar)
        enabled = false
    }
    remapJar {
        val file = shadowJar.get().archiveFile
        archiveClassifier.set(file.hashCode().toString().removePrefix("-"))
        input.set(file)
    }
    shadowJar {
        archiveClassifier.set("dev")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations = listOf(include)

        relocate("org.apache.commons.collections4", "me.cephetir.apache.commons.collections4")

        exclude(
            "**/LICENSE.md",
            "**/LICENSE.txt",
            "**/LICENSE",
            "**/NOTICE",
            "**/NOTICE.txt",
            "pack.mcmeta",
            "dummyThing",
            "**/module-info.class",
            "META-INF/proguard/**",
            "META-INF/maven/**",
            "META-INF/versions/**",
            "META-INF/com.android.tools/**",
            "fabric.mod.json"
        )
        mergeServiceFiles()
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.7"

            freeCompilerArgs =
                listOf(
                    "-Xjvm-default=all",
                    "-Xbackend-threads=0",
                    //"-Xuse-k2"
                )
        }
        kotlinDaemonJvmArguments.set(
            listOf(
                "-Xmx4G",
                "-Dkotlin.enableCacheBuilding=true",
                "-Dkotlin.useParallelTasks=true",
                "-Dkotlin.enableFastIncremental=true"
            )
        )
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}