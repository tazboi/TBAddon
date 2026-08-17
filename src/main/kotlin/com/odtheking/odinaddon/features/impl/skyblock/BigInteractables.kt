package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module
import com.odtheking.odinaddon.mixin.BlockInvoker
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
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
    val buttons by BooleanSetting("Buttons", false, desc = "Changes the hitbox of buttonblocks")
    private val buttonSettings by DropdownSetting(
        "Button Settings",
        false,
        desc = "Changes the size of buttons"
    ).withDependency { buttons }
    val buttonWidth by NumberSetting(
        "Button Width",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the width of buttons (1/16 block step)"
    ).withDependency { buttonSettings }
    val buttonHeight by NumberSetting(
        "Button Height",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the height of buttons (1/16 block step)"
    ).withDependency { buttonSettings }
    val buttonDepth by NumberSetting(
        "Button Depth",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the Depth of buttons (1/16 block step)"
    ).withDependency { buttonSettings }

    val levers by BooleanSetting("Levers", false, desc = "Changes the hitbox of lever blocks")
    private val leverSettings by DropdownSetting(
        "Lever Settings",
        false,
        desc = "Changes the size of levers"
    ).withDependency { levers }
    val leverWidth by NumberSetting(
        "Lever Width",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the width of levers (1/16 block step)"
    ).withDependency { leverSettings }
    val leverHeight by NumberSetting(
        "Lever Height",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the height of levers (1/16 block step)"
    ).withDependency { leverSettings }
    val leverDepth by NumberSetting(
        "Lever Depth",
        default = 1.0,
        min = 0.0625,
        max = 1,
        increment = 0.0625,
        desc = "Changes the Depth of levers (1/16 block step)"
    ).withDependency { leverSettings }
    val fullBlockFloors by BooleanSetting(
        "Full Floor Blocks",
        false,
        desc = "Changes the hitbox of levers/buttons on floors/ceilings to 1x1x1 blocks"
    ).withDependency { levers || buttons }

    /**
     * Returns the custom attachment shapes for blocks.
     * Currently implemented types are [ButtonBlock] and [LeverBlock]
     * Other types should be modified within the return when(block)
     */
    fun getCustomAttachmentShapes(
        block: Block,
        widthNormalized: Double,
        heightNormalized: Double,
        depthNormalized: Double,
        fullBlockFloors: Boolean = false
    ): Function<BlockState, VoxelShape> {
        val blockWidth = widthNormalized.coerceIn(0.0625, 1.0) * 16.0
        val blockHeight = heightNormalized.coerceIn(0.0625, 1.0) * 16.0
        val blockDepth = depthNormalized.coerceIn(0.0625, 1.0) * 16.0

        val shape = Block.box(
            8.0 - blockWidth / 2.0,
            8.0 - blockHeight / 2.0,
            16.0 - blockDepth,
            8.0 + blockWidth / 2.0,
            8.0 + blockHeight / 2.0,
            16.0
        )

        return when (block) {
            is ButtonBlock -> getAttachFaceShapes(
                block,
                shape,
                ButtonBlock.FACE,
                ButtonBlock.FACING,
                fullBlockFloors,
                ButtonBlock.POWERED
            )

            is LeverBlock -> getAttachFaceShapes(
                block,
                shape,
                LeverBlock.FACE,
                LeverBlock.FACING,
                fullBlockFloors,
                LeverBlock.POWERED
            )

            else -> shapesForStates(block, { shape }) //for if further impl is necessary, only currently buttons/levers.
        }
    }

    /**
     * Currently unused. Should be applied if custom user-defined (not 1x1x1) shapes are added to blocks.
     * [ignoredProperties] should be used when applying custom properties, such as POWERED on levers/buttons
     */
    private fun getAttachFaceShapes(
        block: Block,
        shape: VoxelShape,
        faceProperty: Property<AttachFace>,
        facingProperty: Property<Direction>,
        fullBlockFloors: Boolean,
        vararg ignoredProperties: Property<*>
    ): Function<BlockState, VoxelShape> {
        val shapeMap = Shapes.rotateAttachFace(shape)

        val shapeGetter = Function<BlockState, VoxelShape> { state ->
            val face = state.getValue(faceProperty)
            val facing = state.getValue(facingProperty)
            when (face) {
                AttachFace.WALL -> shapeMap[face]!![facing]!!
                AttachFace.FLOOR -> if (fullBlockFloors) Shapes.block() else shapeMap[face]!![facing]!!
                AttachFace.CEILING -> if (fullBlockFloors) Shapes.block() else shapeMap[face]!![facing]!!
            }
        }

        return shapesForStates(block, shapeGetter, *ignoredProperties)
    }

    /**
     * Returns the Shapes for face states for a given block. Using built-in [getShapesForEachState] from [Block]
     * Shape getter is often received from [getAttachFaceShapes]
     */
    private fun shapesForStates(
        block: Block,
        shapeGetter: Function<BlockState, VoxelShape>,
        vararg ignoredProperties: Property<*>
    ): Function<BlockState, VoxelShape> {
        return (block as BlockInvoker).invokeGetShapeForEachState(shapeGetter, *ignoredProperties)
    }


}