package cephetir.simplemod.Features;

import cephetir.simplemod.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChestCloser {
    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent keyboardInputEvent) throws InterruptedException {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser && Config.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName())) {
                Thread.sleep(20);
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre mouseInputEvent) throws InterruptedException {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if(Config.chestCloser && Config.isInDungeon && screen instanceof GuiChest) {
            ContainerChest ch = (ContainerChest) ((GuiChest) screen).inventorySlots;
            if ("Large Chest".equals(ch.getLowerChestInventory().getName()) || "Chest".equals(ch.getLowerChestInventory().getName())) {
                Thread.sleep(20);
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }
}
