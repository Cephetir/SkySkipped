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