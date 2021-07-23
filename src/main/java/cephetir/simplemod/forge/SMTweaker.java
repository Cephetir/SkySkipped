package cephetir.simplemod.forge;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class SMTweaker implements IFMLLoadingPlugin{

    @Override
    public String[] getASMTransformerClass() {
//        TGMLibInstaller.ReturnValue tgmLibInitialized = TGMLibInstaller.initialize(Launch.minecraftHome);
//        if (tgmLibInitialized != TGMLibInstaller.ReturnValue.SUCCESSFUL && tgmLibInitialized != TGMLibInstaller.ReturnValue.ALREADY_INITIALIZED)
//            System.out.println("Failed to inject TGMLib.");
//        else
//            System.out.println("Injected TGMLib successfully.");
//
//        if (TGMLibInstaller.isLoaded())
//            return new String[] {"xyz.matthewtgm.tgmlib.tweaker.TGMLibClassTransformer"};
//        return new String[0];
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
