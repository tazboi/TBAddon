package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.mixin.accessors.AbstractContainerScreenAccessor
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW

object AHBZKeybind : Module(
    name = "AH Search Keybind",
    description = "Searches the hovered item on ah/bz."
) {
    val keybind by KeybindSetting("Keybind", GLFW.GLFW_KEY_UNKNOWN, desc = "Keybind to use.")

    init {
        on<GuiEvent.KeyPress> {
            if (screen !is InventoryScreen || keyCode != keybind.value) return@on
            val clickedSlot = (screen as AbstractContainerScreenAccessor).hoveredSlot?.index.takeIf { it in 5 until 45 } ?: return@on
        }
    }

    private fun resolveSearch(item: ItemStack) {

    }

}