package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.getBlockBounds
import com.odtheking.odin.utils.isEtherwarpItem
import com.odtheking.odin.utils.render.drawStyledBox
import com.odtheking.odinaddon.features.impl.skyblock.event.BlockOutlineEvent
import com.odtheking.odinaddon.features.impl.skyblock.event.MouseEvent
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.phys.BlockHitResult

object RenderModifier : Module(
    name = "Agartha Render",
    description = "Changes some default render options."
) {
    val waterOverlay by BooleanSetting("Disable Water Overlay", false, desc = "Prevent water overlay.")
    val cameraBlockClip by BooleanSetting("Disable Camera Clip", false, desc = "Prevents clipping of camera in blocks.")
    val cameraMaxZoom by NumberSetting(
        "F5 Zoom Multiplier",
        1.0.toFloat(),
        0.1,
        10.0,
        0.05,
        desc = "Modifies the zoom from the player in F5 perspective."
    ).withDependency { cameraBlockClip }
    val noBlindness by BooleanSetting(
        "Disable Blindness",
        false,
        desc = "Prevents blindness from lowering render distance."
    )
    private val blockOverlay by BooleanSetting(
        "Block Overlay",
        false,
        desc = "Sets an overlay on a block when hovering it"
    )
    private val color by ColorSetting(
        "Color",
        Colors.MINECRAFT_RED,
        true,
        desc = "The color of the block overlay."
    ).withDependency { blockOverlay }
    private val renderStyle by SelectorSetting(
        "Render Style",
        "Outline",
        listOf("Filled", "Outline", "Filled Outline"),
        desc = "Style of the box."
    ).withDependency { blockOverlay }
    val GUISize by NumberSetting(
        "Custom GUI Size",
        1,
        1,
        4,
        1,
        desc = "Modifies the GUI size of inventories."
    )

    init {
        on<BlockOutlineEvent> {
            if (!blockOverlay) return@on
            cancel()
        }

        on<RenderEvent.Extract> {
            if (!blockOverlay) return@on

            mc.player?.let {
                if (it.mainHandItem.isEtherwarpItem() != null && it.isCrouching) return@on
            }
            val blockLookingAt = (mc.hitResult as? BlockHitResult) ?: return@on
            val pos = blockLookingAt.blockPos.getBlockBounds()?.move(blockLookingAt.blockPos) ?: return@on
                drawStyledBox(
                    pos,
                    color,
                    renderStyle
                )

        }

    }

    fun getCustomInventoryScale(): Double = (GUISize.toDouble() / mc.window.guiScale.toDouble())
    fun transformInventoryMouse(xy: Double, dimension: Int): Double {
        val center = dimension / 2.0
        return center + (xy - center) / getCustomInventoryScale()
    }
    fun transformInventoryDelta(delta: Double): Double = delta / getCustomInventoryScale()
    fun transformInventoryRender(xy: Double, dimension: Int): Double {
        val center = dimension / 2.0
        return center + (xy - center) * getCustomInventoryScale()
    }

    fun scaleGraphics(graphics: GuiGraphicsExtractor, screen: Screen) {
        if (!enabled) return
        if (screen !is AbstractContainerScreen<*>) return

        val scale = getCustomInventoryScale().toFloat()
        val cx = screen.width / 2.0f
        val cy = screen.height / 2.0f

        graphics.pose().apply {
            pushMatrix()
            translate(cx, cy)
            scale(scale, scale)
            translate(-cx, -cy)
        }
    }

    fun unscaleGraphics(graphics: GuiGraphicsExtractor, screen: Screen) {
        if (!enabled) return
        if (screen !is AbstractContainerScreen<*>) return

        graphics.pose().popMatrix()
    }
}