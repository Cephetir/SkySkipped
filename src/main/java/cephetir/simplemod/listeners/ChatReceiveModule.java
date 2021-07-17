package cephetir.simplemod.listeners;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is used to handle many different {@link ClientChatReceivedEvent}-consuming methods
 * without having to add them all to the {@link MinecraftForge#EVENT_BUS}.
 * <p>
 *
 * @see ChatModule
 */
public interface ChatReceiveModule extends ChatModule {

    /**
     * Place your code here. Called when a Forge {@link ClientChatReceivedEvent} is received.
     * <p>
     * If the event is cancelled, {@link ChatModule}s after that event will not execute. Therefore,
     * {@link ClientChatReceivedEvent#isCanceled()}} checks are unnecessary.
     *
     * @param event a {@link ClientChatReceivedEvent}
     */
    void onMessageReceived(@NotNull ClientChatReceivedEvent event);

}