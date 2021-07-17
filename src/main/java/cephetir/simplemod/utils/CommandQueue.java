package cephetir.simplemod.utils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Sk1er
 */
public class CommandQueue {
    private final Runnable EMPTY = () -> {
    }; //An empty runnable to be substituted when none is provided

    private final Queue<QueueObject> commands = new ConcurrentLinkedQueue<>(); //Queue of messages to ber sent and their corresponding callbacks when sent
    private long delay = 20; //Delay in ticks between messages
    private int tick; //Counter used to keep track of how many ticks since the last message

    public void queue(String chat, Runnable task) {
        commands.add(new QueueObject(chat, task));
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (tick < delay) {
            tick++;
        }

        if (tick % delay == 0) {
            final QueueObject poll = commands.poll();
            if (poll == null || poll.message == null) {
                return;
            }

            tick = 0;

            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage(poll.message);
            }

            poll.runnable.run();
        }
    }

    public void queue(String message) {
        queue(message, EMPTY);
    }

    /**
     * Sets the delay between messages. Used when the user is a YT Rank / Staff in order to remove unnecessary delays
     *
     * @param delay delay in ticks
     */
    public void setDelay(long delay) { //TODO call this when necessary
        this.delay = delay;
    }

    static class QueueObject {
        final String message;
        final Runnable runnable;

        public QueueObject(String message, Runnable runnable) {
            this.message = message;
            this.runnable = runnable;
        }
    }
}