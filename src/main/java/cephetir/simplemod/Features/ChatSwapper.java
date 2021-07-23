package cephetir.simplemod.Features;

import cephetir.simplemod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class ChatSwapper {
    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if(Config.chatSwapper){
            if (event.message.getUnformattedText().startsWith("You have been kicked from the party") || event.message.getUnformattedText().contains("has disbanded") || event.message.getUnformattedText().startsWith("You left the party")) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat all");
            } else if(event.message.getUnformattedText().startsWith("You joined") || event.message.getUnformattedText().startsWith("Party M")){
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat p");
            }
        }
    }
}