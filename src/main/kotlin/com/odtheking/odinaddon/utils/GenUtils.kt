package com.odtheking.odinaddon.utils

import com.odtheking.odin.utils.skyblock.dungeon.ScanUtils
import com.odtheking.odin.utils.skyblock.dungeon.tiles.RoomData

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

