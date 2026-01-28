package com.redcraft86.blockreplacer;

import java.util.Map;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

@Mod(BlockReplacer.MOD_ID)
public final class BlockReplacer {
    public static final String MOD_ID = "blockreplacer";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<Block, BlockReplacement> REPLACEMENTS = new Object2ObjectOpenHashMap<>();

    public BlockReplacer() {
        processConfig();
    }

    public static void processConfig() {
        final Map<Block, Block> mappings = new Object2ObjectOpenHashMap<>();
        ModConfig.get().replaceBlocks.forEach((toId, targets) -> {
            Block toBlock = getBlockFromId(toId);
            if (toBlock == null) {
                return;
            }

            targets.forEach(fromId -> {
                Block fromBlock = getBlockFromId(fromId);
                if (fromBlock != null && !isSupported(fromBlock, toBlock, mappings)) {
                    REPLACEMENTS.put(fromBlock, new BlockReplacement(toBlock));
                    mappings.put(fromBlock, toBlock);
                }
            });
        });
    }

    public static boolean needsReplacing(BlockState state) {
        return REPLACEMENTS.containsKey(state.getBlock());
    }

    public static BlockState processBlock(BlockState oldState) {
        if (oldState == null) {
            return null;
        }

        Block currentBlock = oldState.getBlock();
        if (!REPLACEMENTS.containsKey(currentBlock)) {
            return null;
        }

        return REPLACEMENTS.get(currentBlock).replace(oldState);
    }

    private static boolean isSupported(Block from, Block to, Map<Block, Block> checkMappings) {
        if (checkMappings.containsKey(from)) {
            Block replacement = checkMappings.get(from);
            if (replacement != to) {
                LOGGER.error("Multi Replacement: Block {} is already being replaced by {} but wants to also be replaced by {}",
                        getIdFromBlock(from), getIdFromBlock(replacement), getIdFromBlock(to)
                );
                return true;
            }
        }

        if (checkMappings.containsValue(from)) {
            LOGGER.error("Chain Replacement: Block {} will be replaced by {} but it itself needs to replace another block",
                    getIdFromBlock(from), getIdFromBlock(to)
            );
            return true;
        }

        if (checkMappings.containsKey(to)) {
            if (checkMappings.get(to).equals(from)) {
                LOGGER.error("Circular Replacement: Block {} and {} are replacing each other",
                        getIdFromBlock(from), getIdFromBlock(to)
                );
            } else {
                LOGGER.error("Chain Replacement: Block {} is supposed to replace {} but it itself is being replaced by another block",
                        getIdFromBlock(to), getIdFromBlock(from)
                );
            }
            return true;
        }

        return false;
    }

    private static Block getBlockFromId(String stringId) {
        ResourceLocation rl = ResourceLocation.tryParse(stringId);
        if (rl == null) {
            LOGGER.error("'{}' is not a valid block Id", stringId);
            return null;
        }

        Block b = BuiltInRegistries.BLOCK.get(rl);
        if (b == null) {
            LOGGER.error("Block '{}' is not a valid block", stringId);
            return null;
        }

        return b;
    }

    private static ResourceLocation getIdFromBlock(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block == null ? Blocks.AIR : block);
    }
}
