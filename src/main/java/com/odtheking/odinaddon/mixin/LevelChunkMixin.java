package com.odtheking.odinaddon.mixin;


import com.odtheking.odinaddon.features.impl.skyblock.event.WorldEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private static void preSetBlock(BlockPos blockPos, BlockState blockState, int i, CallbackInfoReturnable<BlockState> cir) {
       if (!blockState.isAir()) new WorldEvent.AddBlock(blockState, blockPos).postAndCatch();
    }
}
