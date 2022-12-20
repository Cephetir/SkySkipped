/*
 *
 * MIT License
 *
 * Copyright (c) 2022 Cephetir
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.cephetir.skyskipped.features.impl.hacks

import me.cephetir.bladecore.utils.TextUtils.containsAny
import me.cephetir.bladecore.utils.TextUtils.stripColor
import me.cephetir.skyskipped.config.Cache
import me.cephetir.skyskipped.config.Config
import me.cephetir.skyskipped.features.Feature
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class Blocker : Feature() {

    @SubscribeEvent
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (!Cache.onSkyblock || !Config.block) return
        if (event.entity !== mc.thePlayer || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) return
        val item = mc.thePlayer?.heldItem?.displayName?.stripColor()?.trim() ?: "Nothing"
        if (!item.containsAny(Config.blockList.split(", "))) return
        mc.thePlayer.playSound("random.orb", 0.8f, 1f)
        event.isCanceled = true
    }
}