package com.redcraft86.blockreplacer;

import java.util.Map;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.fml.common.Mod;

@Mod(BlockReplacer.MOD_ID)
public class BlockReplacer {
    public static final String MOD_ID = "blockreplacer";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<Block, Int2ObjectOpenHashMap<Property<?>>> STATE_CACHE = new Reference2ReferenceOpenHashMap<>();
    private static final Map<Block, Block> MAPPINGS = new Object2ObjectOpenHashMap<>();

    public BlockReplacer() {
        processConfig();
    }

    public static void processConfig() {
        ReplaceCfg.get().replaceBlocks.forEach((toId, targets) -> {
            Block toBlock = getBlockFromId(toId);
            if (toBlock == null) {
                return;
            }

            targets.forEach(fromId -> {
                Block fromBlock = getBlockFromId(fromId);
                if (fromBlock != null && !isSupported(fromBlock, toBlock)) {
                    MAPPINGS.put(fromBlock, toBlock);
                }
            });
        });
    }

    @SuppressWarnings("unchecked")
    public static BlockState processBlock(BlockState currentState) {
        if (currentState == null) {
            return currentState;
        }

        Block currentBlock = currentState.getBlock();
        if (!MAPPINGS.containsKey(currentBlock)) {
            return currentState;
        }

        final Block targetBlock = MAPPINGS.get(currentBlock);
        Int2ObjectOpenHashMap<Property<?>> defaultProps = STATE_CACHE.computeIfAbsent(
            targetBlock, k -> {
                Int2ObjectOpenHashMap<Property<?>> properties = new Int2ObjectOpenHashMap<>();
                for (Property<?> property : k.defaultBlockState().getProperties()) {
                    properties.put(property.generateHashCode(), property);
                }
                return properties;
            }
        );

        // Basically look for overlapping properties between the current and targets states.
        // Overlaps from current will override the targets' to preserve as much data as possible.
        BlockState targetState = targetBlock.defaultBlockState();
        for (Property<?> property : currentState.getProperties()) {
            //noinspection rawtypes
            Property prop = defaultProps.get(property.generateHashCode());
            if (prop != null) {
                targetState = targetState.setValue(prop, currentState.getValue(prop));
            }
        }

        return targetState;
    }

    private static boolean isSupported(Block from, Block to) {
        if (MAPPINGS.containsKey(from)) {
            Block replacement = MAPPINGS.get(from);
            if (replacement != to) {
                LOGGER.error("Multi Replacement: Block {} is already being replaced by {} but wants to also be replaced by {}",
                        getIdFromBlock(from), getIdFromBlock(replacement), getIdFromBlock(to)
                );
                return true;
            }
        }

        if (MAPPINGS.containsValue(from)) {
            LOGGER.error("Chain Replacement: Block {} will be replaced by {} but it itself needs to replace another block",
                    getIdFromBlock(from), getIdFromBlock(to)
            );
            return true;
        }

        if (MAPPINGS.containsKey(to)) {
            if (MAPPINGS.get(to).equals(from)) {
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
