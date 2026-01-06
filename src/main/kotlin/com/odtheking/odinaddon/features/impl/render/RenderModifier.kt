package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.features.Module

object RenderModifier: Module(
    name = "Agartha Render",
    description = "Changes some default render options."
) {
    val cameraBlockClip by BooleanSetting("Disable Camera Clip", false, desc = "Prevents clipping of camera in blocks.")
    val waterOverlay by BooleanSetting("Disable Water Overlay", false, desc = "Prevent water overlay.")
    val cameraMaxZoom by NumberSetting("F5 Zoom Multiplier", 1.0.toFloat(), 0.1, 10.0, 0.05, desc = "Modifies the zoom from the player in F5 perspective.").withDependency { cameraBlockClip }
    val noBlindness by BooleanSetting("Disable Blindness", false, desc = "Prevents blindness from lowering render distance.")
    //private val blockOverlay by BooleanSetting("Block Overlay", false, desc = "Sets an overlay on a block when hovering it")


}