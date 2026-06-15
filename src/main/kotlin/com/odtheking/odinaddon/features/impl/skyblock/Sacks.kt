package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.ChatPacketEvent
import com.odtheking.odin.events.RenderEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.itemId
import com.odtheking.odin.utils.sendCommand
import com.odtheking.odin.utils.skyblock.Island
import com.odtheking.odin.utils.skyblock.LocationUtils
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils

object Sacks : Module(
    name = "Sacks",
    description = "Helper for sacks/GFS"
) {

    private val autoGFS by BooleanSetting("Auto", false, "Automatically collects the selected items from sacks.")
    private val delay by NumberSetting("Delay", 1500, 1500, 5000, 100,"MS delay before attempting to gfs again")
    private val dungeonsOnly by BooleanSetting("Dungeons Only", false, "Only fetches from sacks while in dungeons.").withDependency { autoGFS }
    private val superBoom by BooleanSetting("Superboom", false, "Superboom TNT").withDependency { autoGFS }
    private val pearls by BooleanSetting("Ender Pearls", false, "Ender Pearls").withDependency { autoGFS }
    private val jerries by BooleanSetting("Jerries", false, "Inflatable Jerries").withDependency { autoGFS }

    private val SACKS_REGEX = Regex("(?i)^Moved (.+) from your Sacks to your Inventory\\.?$")
    private val RECENT_JOIN_REGEX = Regex("(?i)^You may only use this command after \\d+s on the server!$")
    private var nextGFS = .0

    init {
        on<RenderEvent.Last> {
            if (!LocationUtils.isInSkyblock || mc.screen != null) return@on
            if (dungeonsOnly && !DungeonUtils.inDungeons) return@on
            if (LocationUtils.currentArea.equalsOneOf(Island.Unknown, Island.SinglePlayer, Island.PrivateIsland)) return@on
            if (LocationUtils.currentArea.equalsOneOf(Island.Kuudra, Island.Dungeon) && (DungeonUtils.currentDungeonPlayer.isDead || mc.player!!.abilities.mayfly)) return@on
            if (mc.level == null) {
                nextGFS = nextGFSDelay(7000)
                return@on
            }

            if (superBoom) autoFillItemFromSacks("SUPERBOOM_TNT", "superboom_tnt")
            if (pearls) autoFillItemFromSacks("ENDER_PEARL", "ender pearl", 16)
            if (jerries) autoFillItemFromSacks("INFLATABLE_JERRY", "inflatable jerry")
        }

        on<ChatPacketEvent> {
            when {
                value.matches(RECENT_JOIN_REGEX) -> nextGFS = nextGFSDelay(5000)
                value.matches(SACKS_REGEX) -> nextGFS = nextGFSDelay(delay)
            }
        }
    }

    private fun autoFillItemFromSacks(itemId: String, sackName: String, stackSize: Int = 64) {
        if (System.currentTimeMillis() < nextGFS) return
        val item = mc.player?.inventory?.find { it?.itemId == itemId }
        val maxStackSize = item?.maxStackSize ?: stackSize
        val count = item?.count ?: 0

        if (count == maxStackSize) return

        nextGFS = nextGFSDelay(delay)
        sendCommand("gfs $sackName ${maxStackSize - count}")
    }

    private fun nextGFSDelay(inDelay: Int): Double {
        val newDelay = System.currentTimeMillis() + inDelay + ((Math.random() - .5) * 60.0) + 6
        return if (nextGFS > newDelay) nextGFS else newDelay
    }
}