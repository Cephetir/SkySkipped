/*
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * Version 2, December 2004
 *
 * Copyright (C) 2021 Cephetir
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 * TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT THE FUCK YOU WANT TO.
 */

package me.cephetir.skyskipped.features.impl.dungeons;

import QolSkyblockMod.PizzaClient.PizzaClient;
import QolSkyblockMod.PizzaClient.features.skills.macros.builder.MacroBuilder;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PizzaFailSafe extends Feature {
    private int ticks = 0;
    private BlockPos last;
    private boolean called = false;

    public PizzaFailSafe() {
        super("PizzaFailSafe", "Hacks", "Hacks");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Config.failSafe || event.phase != TickEvent.Phase.START || !(Loader.isModLoaded("pizzaclient") && PizzaClient.VERSION.equals("1.1")) || mc.thePlayer == null || mc.theWorld == null)
            return;
        if (last != null) {
            if (checkPos(mc.thePlayer.getPosition())) ticks++;
            else {
                last = mc.thePlayer.getPosition();
                ticks = 0;
            }
        } else last = mc.thePlayer.getPosition();
        if (ticks >= 60 && !called/* && PizzaClient.config.macroKey == 2*/ && MacroBuilder.isToggled()) {
            called = true;
            new Thread(() -> {
                try {
                    MacroBuilder.onKey();
                    if (Config.failsafeJump)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                    Thread.sleep(100);

                    // back
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
                    Thread.sleep(300);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                    Thread.sleep(100);

                    // left
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
                    Thread.sleep(300);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                    Thread.sleep(100);

                    // right
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
                    Thread.sleep(300);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);

                    Thread.sleep(100);
                    if (Config.failsafeJump)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                    MacroBuilder.onKey();
                    called = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private boolean checkPos(BlockPos player) {
        return last.getX() - player.getX() <= 1 && last.getX() - player.getX() >= -1 && last.getY() - player.getY() <= 1 && last.getY() - player.getY() >= -1 && last.getZ() - player.getZ() <= 1 && last.getZ() - player.getZ() >= -1;
    }
}