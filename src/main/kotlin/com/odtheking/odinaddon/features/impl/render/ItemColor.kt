package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Color.Companion.withAlpha
import com.odtheking.odin.utils.ItemRarity
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.getSkyblockRarity
import com.odtheking.odin.utils.loreString
import com.odtheking.odin.utils.matchesOneOf
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.LocationUtils
import com.odtheking.odin.utils.skyblock.SkyblockPlayer
import com.odtheking.odinaddon.features.impl.skyblock.event.HotbarSlotRenderEvent
import com.odtheking.odinaddon.utils.findFirstTextWithColor
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack
import java.util.Optional

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
    private val renderNonRarity by BooleanSetting("Render Non-rarity Items", desc = "Whether to render item rarity on items without an explicit rarity in lore.", default = false)
    private val commonIncluded by BooleanSetting("Non-rarity Commons", desc = "Whether to render items without an explicit rarity that are \"common.\" Disable/reenable module to apply setting.", default = true).withDependency { renderNonRarity }


    private var rarityTextColorSet: Set<TextColor> = mapRarities(commonIncluded)

    init {
        on<GuiEvent.RenderSlot> {
            if (!LocationUtils.isInSkyblock) return@on

            if ("Catacombs" in screen.title.string ||
                "Kuudra" in screen.title.string) {
                return@on
            }

            val item = slot.item
            if (item.isEmpty) return@on

            val baseColor = getSkyblockRarity(item.loreString)?.color
                    ?: Color(resolveRarityFromName(item) ?: return@on)

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
        if (!renderNonRarity) return null

        val name = item.displayName
        return findFirstTextWithColor(name, rarityTextColorSet)?.component2()?.value
    }

    private fun mapRarities(commonIncluded: Boolean = false): Set<TextColor> {
        return ItemRarity.entries.mapNotNull { rarity ->
            if (!commonIncluded && rarity == ItemRarity.COMMON) return@mapNotNull null

            ChatFormatting.getByCode(rarity.colorCode.last())?.let {
                TextColor.fromLegacyFormat(it)
            }
        }.toSet()
    }

    override fun onEnable() {
        super.onEnable()
        rarityTextColorSet = mapRarities(commonIncluded)
    }


}