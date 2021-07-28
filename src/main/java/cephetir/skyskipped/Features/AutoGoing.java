package cephetir.skyskipped.Features;

import cephetir.skyskipped.config.Cache;
import cephetir.skyskipped.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoGoing {
    private Material lastBlock = null;
    @SubscribeEvent
    public void autoGoing(TickEvent.ClientTickEvent event) {
        if((event.phase == TickEvent.Phase.START) && (Config.autoGoing && Cache.isInDungeon)) {
            int i = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX);
            int j = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posY);
            int k = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = Minecraft.getMinecraft().thePlayer.worldObj.getBlockState(blockpos).getBlock();
            if (block1.getMaterial() == Material.portal && lastBlock != Material.portal) {
                for(int g = 0; i < 3; i++) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc GOING!");
                    System.out.println("GOING!");
                }
            }
            lastBlock = block1.getMaterial();
        }
    }
}
