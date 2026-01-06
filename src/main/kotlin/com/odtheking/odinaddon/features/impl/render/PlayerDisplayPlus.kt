package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.features.Module

object PlayerDisplayPlus : Module(
    name = "Player Display+",
    description = "Toggle on/off vanilla HUD features."
) {
    val effects by BooleanSetting("Effects", true, desc = "Whether to render potion effects on-screen.")
    val titles by BooleanSetting("Titles", true, desc = "Whether to render titles.")
    val vignette by BooleanSetting("Vignette", true, desc = "Whether to render vignette (screen darkening).")
    val portal by BooleanSetting("Portal", true, desc = "Whether to render portal overlay.")
    val nausea by BooleanSetting("Nausea", true, desc = "Wehther to render nausea")

}