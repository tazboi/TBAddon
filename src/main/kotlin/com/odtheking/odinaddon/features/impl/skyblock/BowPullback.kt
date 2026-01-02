package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.events.core.onSend
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.handlers.TickTask
import com.odtheking.odin.utils.lore
import com.odtheking.odin.utils.render.textDim
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket
import net.minecraft.network.protocol.game.ServerboundUseItemPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.BowItem
import net.minecraft.world.item.ItemStack


object BowPullback : Module(
    name = "Bow Pullback",
    description = "Informs how long a bull has been pulled back for."
) {

    private val tickHud by HUD(
        "Pullback Hud",
        desc = "Displays number of ticks the server has registered the client has pulled back the bow."
    ) {
        val itemInHand = mc.player?.getItemInHand(InteractionHand.MAIN_HAND)
        if (!itemInHand!!.isPullbackBow || ticksHeld < 0) return@HUD 0 to 0
        textDim("§b\uD83C\uDFF9: ${getColor(ticksHeld, maxTicks)}$ticksHeld", 0, 0)
    }
    private val maxTicks by NumberSetting("Max Ticks", 10, 3, 40, 1, desc = "Max ticks to display.")

    private var lastClientUse = -1
    private var ticksHeld = -1
    private var serverHeld = false

    init {
        onSend<ServerboundUseItemPacket> {
            val item = mc.player?.getItemInHand(hand) ?: return@onSend
            if (!item.isPullbackBow) return@onSend

            lastClientUse = sequence
        }

        onReceive<ClientboundBlockChangedAckPacket> {
            when (sequence) {
                lastClientUse -> serverHeld = true
                0 -> serverHeld = false
            }
            ticksHeld = 0
        }

        TickTask(1, true) {
            if (!serverHeld) return@TickTask
            ticksHeld++
            ticksHeld = ticksHeld.coerceIn(0, maxTicks)
        }

    }

    private fun getColor(ticks: Int, maxTicks: Int): String {
        return when {
            ticks == maxTicks -> "§a"
            ticks >= (maxTicks / 2) -> "§e"
            ticks < (maxTicks / 2) -> "§c"
            else -> "§b"
        }
    }

    inline val ItemStack.isPullbackBow: Boolean
        get() {
            if (this.item !is BowItem) return false

            val lore = this.lore
            val lastLine = lore.lastOrNull()?.string ?: return false
            return lastLine.contains("BOW") &&
                    lore.none { it.string.lowercase().contains("shortbow") }
        }

}