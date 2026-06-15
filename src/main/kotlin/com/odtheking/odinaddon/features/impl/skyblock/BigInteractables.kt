package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module
import com.odtheking.odinaddon.mixin.BlockInvoker
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LeverBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.Function

object BigInteractables : Module(
    name = "Big Interactables",
    description = "Changes hitboxes of some interactables (button, lever, etc.)"
) {
    //blocks divided into 16ths, so boxZ shape builds from 0 to 16.0 on x flat axis and 0 to 16.0 on y flat axis.
    // 0 - 16.0 and 0-16.0 translates to full block size
    val buttons by BooleanSetting("Buttons", false, desc = "Changes the hitbox of buttons to 1x1x1 blocks")
    val levers by BooleanSetting("Levers", false, desc = "Changes the hitbox of levers to 1x1x1 blocks")

    fun getFullBlockShapes(block: Block): Function<BlockState, VoxelShape> {
        return getCustomShapes(block,
             { Shapes.block() }
        )
    }

    private fun getCustomShapes(block: Block, shapeGetter: Function<BlockState, VoxelShape>, vararg ignoredProperties: Property<*>): Function<BlockState, VoxelShape> {
        return (block as BlockInvoker).invokeGetShapeForEachState(shapeGetter, *ignoredProperties)
    }

    /**
     * Currently unused. Should be applied if custom user-defined (not 1x1x1) shapes are added to blocks.
     * [ignoredProperties] should be used when applying custom properties, such as POWERED on levers/buttons
     */
    private fun getAttachFaceShapes(
        block: Block, shape: VoxelShape,
        faceProperty: Property<AttachFace>, facingProperty: Property<Direction>,
        vararg ignoredProperties: Property<*>
    ): Function<BlockState, VoxelShape> {
        val shapeMap = Shapes.rotateAttachFace(shape)

        val shapeGetter = Function<BlockState, VoxelShape> { state ->
            shapeMap[state.getValue(faceProperty)]!![state.getValue(facingProperty)]!!
        }

        return getCustomShapes(block, shapeGetter, *ignoredProperties)
    }


}