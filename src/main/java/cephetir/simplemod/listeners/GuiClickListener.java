package cephetir.simplemod.listeners;

import net.minecraftforge.client.event.GuiScreenEvent;

public interface GuiClickListener {
    void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) throws InterruptedException;
}
