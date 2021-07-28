package cephetir.skyskipped.discordrpc;

import cephetir.skyskipped.config.Config;
import lombok.Getter;

public class Client {

    @Getter
    private static final Client INSTANCE = new Client();

    @Getter
    private final DiscordRPCManager discordRPCManager = new DiscordRPCManager();

    public void init() {
        if (Config.DRPC) {
            discordRPCManager.start();
        }
        //update();
    }

    public void shutdown() {
        discordRPCManager.stop();
    }

    public void update() {
//        new Thread(() -> {
//            while(true) {
//                if (Config.DRPC && (!discordRPCManager.isActive())) {
//                    discordRPCManager.start();
//                } else if ((!Config.DRPC) && discordRPCManager.isActive()) {
//                    discordRPCManager.stop();
//                }
//            }
//        }).start();
    }
}
