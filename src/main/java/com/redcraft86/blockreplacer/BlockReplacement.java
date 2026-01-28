package com.redcraft86.blockreplacer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockReplacement(Block newBlock) {
    private static final Object2ObjectOpenHashMap<Block, Int2ObjectOpenHashMap<Property<?>>> PROPERTY_CACHE
            = new Object2ObjectOpenHashMap<>();

    @SuppressWarnings("unchecked")
    public BlockState replace(BlockState oldState) {
        Int2ObjectOpenHashMap<Property<?>> newProps = PROPERTY_CACHE.computeIfAbsent(newBlock, k -> {
            Int2ObjectOpenHashMap<Property<?>> properties = new Int2ObjectOpenHashMap<>();
            for (Property<?> property : newBlock.defaultBlockState().getProperties()) {
                properties.put(property.generateHashCode(), property);
            }
            return properties;
        });

        BlockState mergedState = newBlock.defaultBlockState();
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
