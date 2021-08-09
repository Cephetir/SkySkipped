/*
 * SkySkipped - Hypixel Skyblock mod
 * Copyright (C) 2021  Cephetir
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

package cephetir.skyskipped.Features;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ChatSwapper {
    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if(Config.chatSwapper){
            if (event.message.getUnformattedText().startsWith("You have been kicked from the party") || event.message.getUnformattedText().contains("has disbanded") || event.message.getUnformattedText().startsWith("You left the party")) {
                if(Cache.inParty) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat all");
                    Cache.inParty = false;
                }
            } else if(event.message.getUnformattedText().startsWith("You have joined") || event.message.getUnformattedText().startsWith("Party M") || event.message.getUnformattedText().contains("joined the ")){
                if(!Cache.inParty) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat p");
                    Cache.inParty = true;
                }
            }
        }
    }
}