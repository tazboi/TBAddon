package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.customData
import com.odtheking.odin.utils.loreString
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.features.impl.skyblock.event.UseItemOnEvent
import net.minecraft.core.component.DataComponentType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack

object ItemHelpers : Module (
    name = "Item Helper",
    description = "Helper for various items (skulls, etc.)"
)  {
    val preventSkullPlace by BooleanSetting(name = "Prevent Skull Place", default = true, desc = "Prevents placing skulls with abilities on blocks.", )

    init {
        on<UseItemOnEvent> {
            if (!preventSkullPlace || item.item !is BlockItem) return@on
            item.loreString.find {
                it.lowercase().contains("ability") &&
                        it.lowercase().contains("right click")
            } ?: return@on

            cancel()
            val player = mc.player ?: return@on
            mc.gameMode?.useItem(player, player.usedItemHand)
        }
    }
}