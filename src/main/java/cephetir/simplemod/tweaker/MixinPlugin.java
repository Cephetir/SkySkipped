package cephetir.simplemod.tweaker;

import net.minecraftforge.fml.relauncher.CoreModManager;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private boolean hasPlayerAPI = false;

    @Override
    public void onLoad(String mixinPackage) {
        for (Map.Entry<String, List<String>> e : CoreModManager.getTransformers().entrySet()) {
            if (e.getKey().startsWith("PlayerAPIPlugin") && e.getValue().contains("api.player.forge.PlayerAPITransformer")) {
                System.out.println("PlayerAPI detected.");
                hasPlayerAPI = true;
                break;
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("cephetir.simplemod.mixins.playerapi.")) {
            return hasPlayerAPI;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
