package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color.Companion.withAlpha
import com.odtheking.odin.utils.getSkyblockRarity
import com.odtheking.odin.utils.loreString
import com.odtheking.odinaddon.features.impl.skyblock.event.HotbarSlotRenderEvent
import net.minecraft.client.player.inventory.Hotbar

object ItemRarity : Module (
    name = "Item Rarity",
    description = "Shows the item rarity background behind items."
) {
    private val opacity by NumberSetting("Opacity", 0.5f, 0.05, 0.95, 0.05, desc = "See-through ability of the rarity background.")

    init {
        //TODO: RenderPipeline for other shapes (circles, etc.)
        on<GuiEvent.DrawSlot> {
            val item = slot.item
            if (item.isEmpty) return@on

            val rarityColor = getSkyblockRarity(item.loreString)?.color ?: return@on
            guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, rarityColor.withAlpha(opacity).rgba)
        }

        on<HotbarSlotRenderEvent> {
            if (item.isEmpty) return@on
            val rarityColor = getSkyblockRarity(item.loreString)?.color ?: return@on
            guiGraphics.fill(x, y, x + 16, y + 16, rarityColor.withAlpha(opacity).rgba)
        }
    }
}