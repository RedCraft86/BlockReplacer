package com.redcraft86.blockreplacer.mixin;

import com.redcraft86.blockreplacer.BlockReplacer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public abstract class MixinLevelChunkSection {

    @Shadow public abstract BlockState setBlockState(int x, int y, int z, BlockState state, boolean useLocks);

    @Inject(
        method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
        at = @At("HEAD"), cancellable = true
    )
    private void sbr_replaceBlockState(int x, int y, int z, BlockState state, boolean useLocks, CallbackInfoReturnable<BlockState> cir) {
        if (BlockReplacer.needsReplacing(state)) {
            cir.setReturnValue(setBlockState(x, y, z, BlockReplacer.processBlock(state), useLocks));
        }
    }
}
