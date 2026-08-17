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
    @Unique private double tbaddon$lastButtonWidth;
    @Unique private double tbaddon$lastButtonHeight;
    @Unique private double tbaddon$lastButtonDepth;
    @Unique private boolean tbaddon$lastFloorsSetting;

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
        //Re-cache on changing custom size/doesn't exist/full block floors setting resets
        if (
                this.tbaddon$customButtonShapes == null ||
                        this.tbaddon$lastButtonWidth != BigInteractables.INSTANCE.getButtonWidth() ||
                        this.tbaddon$lastButtonHeight != BigInteractables.INSTANCE.getButtonHeight()  ||
                        this.tbaddon$lastButtonDepth != BigInteractables.INSTANCE.getButtonDepth() ||
                        this.tbaddon$lastFloorsSetting != BigInteractables.INSTANCE.getFullBlockFloors()
        ) {
            this.tbaddon$lastButtonWidth = BigInteractables.INSTANCE.getButtonWidth();
            this.tbaddon$lastButtonHeight = BigInteractables.INSTANCE.getButtonHeight();
            this.tbaddon$lastButtonDepth = BigInteractables.INSTANCE.getButtonDepth();
            this.tbaddon$lastFloorsSetting = BigInteractables.INSTANCE.getFullBlockFloors();
            this.tbaddon$customButtonShapes = BigInteractables.INSTANCE.getCustomAttachmentShapes(
                    (Block) (Object) this,
                    this.tbaddon$lastButtonWidth,
                    this.tbaddon$lastButtonHeight,
                    this.tbaddon$lastButtonDepth,
                    this.tbaddon$lastFloorsSetting
                    );
        }

        cir.setReturnValue(this.tbaddon$customButtonShapes.apply(blockState));
    }
}
