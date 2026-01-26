package com.redcraft86.blockreplacer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class BlockReplacement {
    private final BlockState newState;
    private Int2ObjectOpenHashMap<Property<?>> newProps;

    public BlockReplacement(Block newBlock) {
        newState = newBlock.defaultBlockState();
        newProps = null;
    }

    public BlockState getNewState() {
        return newState;
    }

    public Block getNewBlock() {
        return newState.getBlock();
    }

    @SuppressWarnings("unchecked")
    public BlockState replace(BlockState oldState) {
        if (newProps == null) {
            newProps = new Int2ObjectOpenHashMap<>();
            for (Property<?> property : newState.getProperties()) {
                newProps.put(property.generateHashCode(), property);
            }
        }

        BlockState mergedState = newState;
        for (Property<?> property : oldState.getProperties()) {
            //noinspection rawtypes
            Property prop = newProps.get(property.generateHashCode());
            if (prop != null) {
                mergedState = mergedState.setValue(prop, oldState.getValue(prop));
            }
        }

        return mergedState;
    }
}
