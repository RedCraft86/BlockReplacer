package com.redcraft86.blockreplacer;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.redcraft86.lanternlib.api.config.*;

public class ReplaceCfg extends JsonConfig {
    public static ReplaceCfg INSTANCE = null;

    public static ReplaceCfg get() {
        if (INSTANCE == null) {
            INSTANCE = JsonConfig.createConfig(ReplaceCfg.class);
        }
        return INSTANCE;
    }

    public ReplaceCfg() {
        super(BlockReplacer.MOD_ID);
    }

    @Config
    @Comment("NOTE: This mod will NOT remove block items. Use KubeJS or Item Obliterator for that.")
    @Comment("      It also WON'T run retroactively, only new placements will get replaced.")
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
