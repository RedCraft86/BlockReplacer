package com.redcraft86.blockreplacer;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;

@Mod(BlockReplacer.MOD_ID)
public class BlockReplacer {
    public static final String MOD_ID = "blockreplacer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BlockReplacer(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        SwapConfig.get();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
