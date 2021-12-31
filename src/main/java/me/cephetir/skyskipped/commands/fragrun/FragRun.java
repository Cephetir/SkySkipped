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

package me.cephetir.skyskipped.commands.fragrun;

import me.cephetir.skyskipped.SkySkipped;
import me.cephetir.skyskipped.config.Config;
import me.cephetir.skyskipped.features.Feature;
import me.cephetir.skyskipped.utils.TextUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class FragRun extends Feature {
    public FragRun() {
        super("FragRunCommands", "Dungeons", "Yes");
    }

    @SubscribeEvent
    public void onMessageReceived(@NotNull ClientChatReceivedEvent event) {
        if (TextUtils.stripColor(event.message.getUnformattedText()).trim().contains("> EXTRA STATS <")) return;
        if (Config.EndLeave) SkySkipped.features.getLeaveCommand().start(false);
    }
}
