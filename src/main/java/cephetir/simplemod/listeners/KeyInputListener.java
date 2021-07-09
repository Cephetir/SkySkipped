package cephetir.simplemod.listeners;

import net.minecraftforge.client.event.GuiScreenEvent;

public interface KeyInputListener {
    void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) throws InterruptedException;
}
