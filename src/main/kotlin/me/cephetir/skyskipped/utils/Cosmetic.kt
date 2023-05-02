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

package me.cephetir.skyskipped.utils

import kotlinx.coroutines.launch
import me.cephetir.bladecore.core.event.BladeEventBus
import me.cephetir.bladecore.core.event.listener.listener
import me.cephetir.bladecore.utils.threading.BackgroundScope
import me.cephetir.skyskipped.SkySkipped
import me.cephetir.skyskipped.config.Config
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.RenderWorldLastEvent
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.imageio.ImageIO


class Cosmetic(val name: String, animatedName: Boolean, private val nicks: List<Nick>, val cape: Cape?) {
    private var time = -1L
    private var currentIndex = 0

    init {
        if (animatedName) {
            listener<RenderWorldLastEvent> {
                val currentTime = System.currentTimeMillis()
                if (time == -1L) time = currentTime
                else if (currentTime - time > nicks[currentIndex].delay) {
                    if (++currentIndex >= nicks.size) currentIndex = 0
                    time = currentTime
                    SkySkipped.cosmeticCache.entries().removeIf { it.key.contains(name) }
                }
            }

            BladeEventBus.subscribe(this)
        }
    }

    fun getNick(): Nick = nicks[currentIndex]

    data class Nick(val nick: String, val prefix: String, val delay: Int)
    data class Cape(val uuid: String, val cape: String) {
        private var resourceLocation: ResourceLocation? = null
        private var loaded = false
        private var capeFile: File? = null

        fun getCape(): ResourceLocation? {
            if (!loaded) {
                BackgroundScope.launch {
                    val dir = File(Config.modDir, "capes")
                    if (!dir.exists()) dir.mkdirs()
                    val file = File(dir, "$uuid.png")

                    URL(cape).openStream().use { Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING) }
                    capeFile = file
                }
                loaded = true
            } else if (capeFile != null && resourceLocation == null)
                resourceLocation = mc.textureManager.getDynamicTextureLocation("skyskipped", DynamicTexture(ImageIO.read(capeFile)))

            return resourceLocation
        }
    }
}