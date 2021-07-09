package cephetir.simplemod.Features;

import cephetir.simplemod.core.Config;
import cephetir.simplemod.listeners.GuiClickListener;
import cephetir.simplemod.listeners.KeyInputListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestCloser implements KeyInputListener, GuiClickListener {
    @SubscribeEvent
    @Override
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) throws InterruptedException {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser) {
            //if(new InDungeon().getIsInDungeon()) {
                if (screen instanceof GuiChest) {
                    ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
                    if (("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))) {
                        Thread.sleep(10);
                        Minecraft.getMinecraft().thePlayer.closeScreen();
                    }
                }
            //}
        }
    }

    @SubscribeEvent
    @Override
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) throws InterruptedException {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser) {
            //if(new InDungeon().getIsInDungeon()) {
                if (screen instanceof GuiChest) {
                    ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
                    if (("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName()))) {
                        Thread.sleep(10);
                        Minecraft.getMinecraft().thePlayer.closeScreen();
                    }
                }
            //}
        }
    }
}
