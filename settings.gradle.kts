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

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.ilarea.ru/snapshots")
        maven("https://jitpack.io")
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "net.minecraftforge.gradle.forge" -> useModule("com.github.asbyth:ForgeGradle:${requested.version}")
                "io.github.juuxel.loom-quiltflower-mini" -> useModule("com.github.Cephetir:loom-quiltflower-mini:${requested.version}")
            }
        }
    }
}

rootProject.name = "SkySkipped"