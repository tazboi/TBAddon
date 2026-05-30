package com.odtheking.odinaddon.utils

import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.skyblock.dungeon.ScanUtils
import com.odtheking.odin.utils.skyblock.dungeon.tiles.RoomData
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

fun getRoomData(x: Number, z: Number): RoomData? =
    ScanUtils.coreToRoomData[
        ScanUtils.getCore(
            ScanUtils.getRoomCenter(
                x.toInt(), z.toInt()
            )
        )
    ]

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

