package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.CancellableEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.itemId
import com.odtheking.odin.utils.itemUUID
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odinaddon.features.impl.skyblock.event.DropEvent
import com.odtheking.odinaddon.features.impl.skyblock.event.SlotInteractEvent
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack

object ProtectItem : Module(
    name = "Item Protect",
    description = "Protects selected items from being dropped."
) {
    private val preventDrop by BooleanSetting("Dropping", true, desc = "Prevent dropping whitelisted items.")
    var itemList by ListSetting("Protected Items Map", mutableListOf<ProtectedItem>())

    init {
        // TODO: Experiment with drop packet instead of letting the player drop the item themself client-side.
        on<DropEvent> {
            if (!preventDrop || item == null) return@on
            if (DungeonUtils.inDungeons && !DungeonUtils.dungeonTeammates.isEmpty()) {
                return@on
                //val action = if (all) ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS else ServerboundPlayerActionPacket.Action.DROP_ITEM
                //mc.player?.connection?.send(ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN))
            }
            tryPreventDrop(item, this)
        }

        on<SlotInteractEvent> {
            if (!preventDrop || clickType != ClickType.THROW || (DungeonUtils.inDungeons && !DungeonUtils.dungeonTeammates.isEmpty())) return@on

            val menu = (screen as? AbstractContainerScreen<*>)?.menu ?: return@on
            val slot = menu.getSlot(slotId)
            val item = slot.item

            tryPreventDrop(item, this)
        }

    }

    private fun tryPreventDrop(item: ItemStack, event: CancellableEvent) {
        if (itemList.any { it.uuid == item.itemUUID || it.sbID == item.itemId || it.name == item.customName?.string}) {
            modMessage(
                Component.literal("Prevented dropping ")
                .append(item.customName ?: item.hoverName)
                .append(Component.literal("."))
            )
            event.cancel()
        }
    }

    data class ProtectedItem(val name: String?, val sbID: String, val uuid: String? = null)
}