package com.odtheking.odinaddon.utils

import com.odtheking.odin.OdinMod
import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.features.impl.dungeon.map.WorldScan
import com.odtheking.odin.features.impl.dungeon.map.tile.RoomData
import com.odtheking.odin.utils.IVec2
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

fun getRoomData(x: Int, z: Int): RoomData? {
    val chunk = mc.level?.getChunk(x shr 4, z shr 4) ?: return null
    //core, highestblock
    val (core, _) = WorldScan.getRoomCore(chunk, IVec2(x * 32 - 185, z * 32 - 185))
    return RoomData.getRoomData(core)

}

fun getColor(ticks: Int, maxTicks: Int, countdown: Boolean = false): String {
    return when {
        ticks == maxTicks -> if (!countdown) "§a" else "§c" // green if counting up, red if down
        ticks >= (maxTicks / 2) -> "§e"
        ticks < (maxTicks / 2) -> if (!countdown) "§c" else "§a"
        else -> "§b"
    }
}

fun findFirstTextWithColor(component: Component, targetColors: Set<TextColor>): Pair<String, TextColor>? {
    var found: Pair<String, TextColor>? = null

    component.visit({ style: Style, text: String ->
        val color = style.color

        if (color != null && color in targetColors) {
            found = text to color
            return@visit Optional.of(Unit)
        }

        Optional.empty<Unit>()
    }, Style.EMPTY)

    return found
}

fun String.removeUnicode(): String {
    return this.replace(Regex("[^A-Za-z0-9 ]"), "")
}

