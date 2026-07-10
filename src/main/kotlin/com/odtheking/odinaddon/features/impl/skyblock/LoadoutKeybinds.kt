package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.clickgui.settings.impl.KeybindSetting
import com.odtheking.odin.events.ScreenEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.clickSlot
import com.odtheking.odin.utils.containsOneOf
import com.odtheking.odin.utils.sendCommand
import com.odtheking.odin.utils.skyblock.LocationUtils
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import org.lwjgl.glfw.GLFW

object LoadoutKeybinds : Module(
    name = "Loadout Binds",
    description = "Binds for loadouts, wardrobe, etc."
) {
    //Remove when Odin commands point to correct commands
    private val wardrobe by KeybindSetting("Wardrobe", GLFW.GLFW_KEY_UNKNOWN, desc = "Keybind to open wardrobe.").onPress {
        if (!enabled || !LocationUtils.isInSkyblock) return@onPress
        sendCommand("wardrobe")
    }

    private val loadouts by KeybindSetting("Loadouts", GLFW.GLFW_KEY_UNKNOWN, desc = "Keybind to open loadouts menu.").onPress {
        if (!enabled || !LocationUtils.isInSkyblock) return@onPress
        sendCommand("loadouts")
    }

    private val equipment by KeybindSetting("Equipment/Stats", GLFW.GLFW_KEY_UNKNOWN, desc = "Keybind to open stats.").onPress {
        if (!enabled || !LocationUtils.isInSkyblock) return@onPress
        sendCommand("stats")
    }

    private val stopUnequip by BooleanSetting("Stop Unequip", false, desc = "Stops from unequipping armor in wardrobe.")

    private val advanced by DropdownSetting("Show Settings")
    private val wardrobe1 by KeybindSetting("Default Slot 1", GLFW.GLFW_KEY_1, desc = "Keybind to equip the first wardrobe slot.").withDependency { advanced }
    private val wardrobe2 by KeybindSetting("Default Slot 2", GLFW.GLFW_KEY_2, desc = "Keybind to equip the second wardrobe slot.").withDependency { advanced }
    private val wardrobe3 by KeybindSetting("Default Slot 3", GLFW.GLFW_KEY_3, desc = "Keybind to equip the third wardrobe slot.").withDependency { advanced }
    private val wardrobe4 by KeybindSetting("Default Slot 4", GLFW.GLFW_KEY_4, desc = "Keybind to equip the fourth wardrobe slot.").withDependency { advanced }
    private val wardrobe5 by KeybindSetting("Default Slot 5", GLFW.GLFW_KEY_5, desc = "Keybind to equip the fifth wardrobe slot.").withDependency { advanced }
    private val wardrobe6 by KeybindSetting("Default Slot 6", GLFW.GLFW_KEY_6, desc = "Keybind to equip the sixth wardrobe slot.").withDependency { advanced }
    private val wardrobe7 by KeybindSetting("Default Slot 7", GLFW.GLFW_KEY_7, desc = "Keybind to equip the seventh wardrobe slot.").withDependency { advanced }
    private val wardrobe8 by KeybindSetting("Default Slot 8", GLFW.GLFW_KEY_8, desc = "Keybind to equip the eighth wardrobe slot.").withDependency { advanced }
    private val wardrobe9 by KeybindSetting("Default Slot 9", GLFW.GLFW_KEY_9, desc = "Keybind to equip the ninth wardrobe slot.").withDependency { advanced }
    private val nextPage by KeybindSetting("Next Page", GLFW.GLFW_KEY_D, desc = "Keybind to go to the next page of wardrobe.").withDependency { advanced }
    private val prevPage by KeybindSetting("Prev Page", GLFW.GLFW_KEY_A, desc = "Keybind to go to the previous page of wardrobe.").withDependency { advanced }


    private const val MIN_INVENTORY_SIZE = 0
    private const val MAX_INVENTORY_SIZE = 53

    private const val WARDROBE_SLOT_OFFSET = 36
    private const val LOADOUTS_SLOT_SET_OFFSET = 14 //set of 3 slots

    //Shoutout Hypixel not being consistent in their UIs
    private const val WARDROBE_PREV_PAGE_SLOT = 45
    private const val WARDROBE_NEXT_PAGE_SLOT = 53

    private const val LOADOUTS_PREV_PAGE_SLOT = 17
    private const val LOADOUTS_NEXT_PAGE_SLOT = 44

    init {
        on<ScreenEvent.KeyPress> {
            if (!onKey((screen as? AbstractContainerScreen<*>) ?: return@on, input.key, this)) return@on
            cancel()
        }
    }

    private fun onKey(screen: AbstractContainerScreen<*>, key: Int, event: ScreenEvent.KeyPress): Boolean {
        val screenTitle = screen.title.string.takeIf{ it.containsOneOf("Loadouts", "Armor Sets")} ?: return false
        if (checkPageClick(screen, key)) return true

        val slotIndex = arrayOf(wardrobe1, wardrobe2, wardrobe3, wardrobe4, wardrobe5, wardrobe6, wardrobe7, wardrobe8, wardrobe9).indexOfFirst {
            it.value == key
        }.takeIf { it != -1 } ?: return false

        val toClick = when {
            "Loadouts" in screenTitle -> slotIndex.loadoutSlot
            "Armor Sets" in screenTitle -> slotIndex.wardrobeSlot
            else -> return false
        }

        val equippedSlot = screen.menu.slots.find { "Equipped" in it.item.hoverName.string}?.index
        if (equippedSlot == slotIndex.wardrobeSlot && stopUnequip) return true

        mc.player?.clickSlot(screen.menu.containerId, toClick)
        return true
    }

    private fun checkPageClick(screen: AbstractContainerScreen<*>, key: Int): Boolean {
        if (key != nextPage.value && key != prevPage.value) return false

        val toClick = when {
            "Loadouts" in screen.title.string -> if (key == prevPage.value) LOADOUTS_PREV_PAGE_SLOT else LOADOUTS_NEXT_PAGE_SLOT
            "Armor Sets" in screen.title.string -> if (key == prevPage.value) WARDROBE_PREV_PAGE_SLOT else WARDROBE_NEXT_PAGE_SLOT
            else -> return false
        }

        mc.player?.clickSlot(screen.menu.containerId, toClick)
        return true
    }

    private val Int.wardrobeSlot
        get() = (this + WARDROBE_SLOT_OFFSET).coerceIn(MIN_INVENTORY_SIZE, MAX_INVENTORY_SIZE)

    private val Int.loadoutSlot
        get() = (this + LOADOUTS_SLOT_SET_OFFSET + 6 * (this / 3)).coerceIn(MIN_INVENTORY_SIZE, MAX_INVENTORY_SIZE)

}

