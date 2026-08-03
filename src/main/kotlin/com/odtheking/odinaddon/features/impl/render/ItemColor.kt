package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Color.Companion.withAlpha
import com.odtheking.odin.utils.ItemRarity
import com.odtheking.odin.utils.getSkyblockRarity
import com.odtheking.odin.utils.loreString
import com.odtheking.odin.utils.skyblock.LocationUtils
import com.odtheking.odinaddon.features.impl.skyblock.event.HotbarSlotRenderEvent
import com.odtheking.odinaddon.utils.findFirstTextWithColor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack

object ItemColor : Module(
    name = "Item Rarity",
    description = "Shows the item rarity background behind items."
) {
    private val opacity by NumberSetting(
        "Opacity",
        0.5f,
        0.05,
        0.95,
        0.05,
        desc = "See-through ability of the rarity background."
    )

    private var rarityTextColorSet: Set<TextColor> = mapRarities()

    init {
        on<GuiEvent.RenderSlot> {
            if (!LocationUtils.isInSkyblock) return@on

            val item = slot.item
            if (item.isEmpty) return@on

            val baseColor = getSkyblockRarity(item.loreString)?.color
                    ?: if ("[Lvl" in item.displayName.string) //special case for pets
                        Color(resolveRarityFromName(item) ?: return@on)
                    else return@on

            val rarityColor = baseColor.withAlpha(opacity).rgba
            guiGraphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, rarityColor)
        }


        on<HotbarSlotRenderEvent> {
            if (!LocationUtils.isInSkyblock) return@on
            if (item.isEmpty) return@on

            val rarityColor = getSkyblockRarity(item.loreString)?.color ?: return@on
            guiGraphics.fill(x, y, x + 16, y + 16, rarityColor.withAlpha(opacity).rgba)
        }
    }

    private fun resolveRarityFromName(item: ItemStack): Int? {
        val name = item.displayName
        return findFirstTextWithColor(name, rarityTextColorSet)?.component2()?.value
    }

    private fun mapRarities(): Set<TextColor> {
        return ItemRarity.entries.mapNotNull { rarity ->
            ChatFormatting.getByCode(rarity.colorCode.last())?.let {
                TextColor.fromLegacyFormat(it)
            }
        }.toSet()
    }



}