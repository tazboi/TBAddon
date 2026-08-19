package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ListSetting
import com.odtheking.odin.events.core.CancellableEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.createSoundSettings
import com.odtheking.odin.utils.itemId
import com.odtheking.odin.utils.itemUUID
import com.odtheking.odin.utils.lore
import com.odtheking.odin.utils.loreString
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.playSoundSettings
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odinaddon.features.impl.skyblock.ProtectItem.itemList
import com.odtheking.odinaddon.features.impl.skyblock.event.DropEvent
import com.odtheking.odinaddon.features.impl.skyblock.event.SlotInteractEvent
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack

object ProtectItem : Module(
    name = "Item Protect",
    description = "Protects selected items from being dropped."
) {
    private val sendSound by BooleanSetting(
        "Drop Cancel Sound",
        false,
        desc = "Send sound instead of message when an item is dropped."
    )
    private val soundSettings = createSoundSettings("Drop Sound", "entity.blaze.hurt") { sendSound }
    var itemList = this.registerSetting(ListSetting("Protected Items Map", mutableListOf<ProtectedItem>())).value
    private var lastPlayed = 0L

    init {
        // TODO: Experiment with drop packet instead of letting the player drop the item themself client-side.
        on<DropEvent> {
            if (item == null) return@on
            if (DungeonUtils.inDungeons) {
                //val action = if (all) ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS else ServerboundPlayerActionPacket.Action.DROP_ITEM
                //mc.player?.connection?.send(ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN))
                return@on
            }
            tryPreventDrop(item, this)
        }

        on<SlotInteractEvent> {
            val menu = (screen as? AbstractContainerScreen<*>)?.menu ?: return@on
            val inAH = screen.title.string.contains("Auction") //may need to be changed in future
            val sellable = menu.slots.any {
                it.item.loreString.contains("Click to buyback!") ||
                it.item.customName?.string?.contains("Sell Item") ?: false
            }

            val slot = menu.getSlot(slotId)
            val item = slot.item.takeIf { it != ItemStack.EMPTY } ?: return@on

            if (!inAH && !item.loreString.contains("pickup") && !sellable && clickType != ContainerInput.THROW) return@on

            tryPreventDrop(item, this)
        }

    }

    private fun tryPreventDrop(item: ItemStack, event: CancellableEvent) {
        val foundItem =
            if (item.itemUUID.isEmpty()) itemList.find {
                (item.itemId.isNotEmpty() && it.sbID == item.itemId) ||
                        it.name == item.customName?.string
            }
            else itemList.find { it.uuid == item.itemUUID }
        if (foundItem == null) return

        val now = System.currentTimeMillis()
        if (!sendSound) modMessage(
            Component.literal("Prevented dropping ")
                .append(item.customName ?: item.hoverName)
                .append(Component.literal("."))
        ) else if (now - lastPlayed > 100) {
            playSoundSettings(soundSettings())
            lastPlayed = now
        }
        event.cancel()
    }

    data class ProtectedItem(val name: String?, val sbID: String, val uuid: String? = null)
}