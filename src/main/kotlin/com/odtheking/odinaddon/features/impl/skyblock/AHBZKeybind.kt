package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.features.Module
import org.lwjgl.glfw.GLFW

object AHBZKeybind : Module(
    name = "AH Search Keybind",
    description = "Searches the hovered item on ah/bz."
) {
    val keybind by KeybindSetting("Keybind", GLFW.GLFW_KEY_UNKNOWN, desc = "Keybind to use.")


}