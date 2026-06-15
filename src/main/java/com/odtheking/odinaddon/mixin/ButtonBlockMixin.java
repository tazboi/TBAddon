package com.odtheking.odinaddon.mixin;

import com.odtheking.odinaddon.features.impl.skyblock.BigInteractables;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin {

    @Unique
    private Function<BlockState, VoxelShape> tbaddon$customButtonShapes;

    @Inject(
            method = "getShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tbaddon$onButtonShape(
            BlockState blockState,
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (!BigInteractables.INSTANCE.getEnabled() || !BigInteractables.INSTANCE.getButtons()) return;
        if (this.tbaddon$customButtonShapes == null) {
            this.tbaddon$customButtonShapes = BigInteractables.INSTANCE.getFullBlockShapes((Block) (Object) this);
        }

        cir.setReturnValue(this.tbaddon$customButtonShapes.apply(blockState));
    }
}
