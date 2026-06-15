package com.odtheking.odinaddon.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(Block.class)
public interface BlockInvoker {

    @Invoker("getShapeForEachState")
    Function<BlockState, VoxelShape> invokeGetShapeForEachState(
            Function<BlockState, VoxelShape> shapeGetter,
            Property<?>... ignoredProperties
    );

}
