package com.redcraft86.blockreplacer;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.redcraft86.lanternlib.api.config.*;
import com.redcraft86.lanternlib.api.config.annotations.*;

public final class ModConfig extends JsonConfig {
    public static ModConfig INSTANCE = null;

    public static ModConfig get() {
        if (INSTANCE == null) {
            INSTANCE = JsonConfig.createConfig(ModConfig.class);
        }
        return INSTANCE;
    }

    public ModConfig() {
        super(BlockReplacer.MOD_ID);
    }

    @Config
    @Comment("NOTE: This mod will NOT remove block items. Use KubeJS, Reliable Remover, or Item Obliterator for that.")
    @Comment("      This system will NOT run retroactively either, only new placements will get replaced.")
    @Comment()
    @Comment("Uses a list for values but can be a single entry list if needed.")
    @Comment("Example 1: ")
    @Comment("\t\"minecraft:new_block\": [ \"minecraft:old_block\" ]")
    @Comment("Example 2: ")
    @Comment("\t\"minecraft:new_block\": [")
    @Comment("\t\t\"minecraft:old_block_1\",")
    @Comment("\t\t\"minecraft:old_block_2\",")
    @Comment("\t\t\"minecraft:old_block_etc\"")
    @Comment("\t]")
    public Map<String, List<String>> replaceBlocks = new HashMap<>();
}
