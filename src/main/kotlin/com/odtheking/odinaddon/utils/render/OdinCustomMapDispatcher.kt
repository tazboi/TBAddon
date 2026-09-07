package com.odtheking.odinaddon.utils.render

import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.features.impl.dungeon.map.DungeonMap
import com.odtheking.odin.features.impl.dungeon.map.DungeonScan
import com.odtheking.odin.features.impl.dungeon.map.fillRoom
import com.odtheking.odin.features.impl.dungeon.map.roomTypeColor
import com.odtheking.odin.features.impl.dungeon.map.tile.*
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.Color.Companion.darker
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.IVec2
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.handlers.TickTask
import com.odtheking.odinaddon.features.impl.dungeon.SillyMap
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

fun GuiGraphicsExtractor.customRenderRoomText(room: DungeonRoom) {
    if (room.type.equalsOneOf(RoomType.FAIRY, RoomType.BLOOD)) return

    val (cx, cz) = room.center?.let { center -> center.x to center.z} ?: return
    val fontHeight = mc.font.lineHeight
    val secretsTxt = if ((room.data?.maxSecrets ?: 0) > 0) " ${room.foundSecrets ?: "0"}/${room.data?.maxSecrets}" else ""
    val renderLines = when (DungeonMap.roomText) {
        0 -> "${room.name}$secretsTxt"
        1 -> room.name
        else -> secretsTxt
    }?.trim()?.split(" ") ?: return
    val renderLinesHeight = (renderLines.size - 1) * fontHeight * DungeonMap.textScaling

    val textColor = when (room.checkmark) {
        MapCheckmark.GREEN -> Colors.MINECRAFT_GREEN
        MapCheckmark.WHITE -> Colors.WHITE
        MapCheckmark.RED -> Colors.MINECRAFT_RED
        else -> Color(100, 100, 100)
    }.rgba

    for ((i, line) in renderLines.withIndex()) {
        val renderY = cz.toFloat() - renderLinesHeight / 2f + i * fontHeight * DungeonMap.textScaling
        pose().pushMatrix()
        pose().translate(cx.toFloat(), renderY)
        pose().scale(DungeonMap.textScaling)
        centeredText(mc.font, line, 0, -fontHeight / 2, textColor)
        pose().popMatrix()
    }
}

fun customRenderRooms(graphics: GuiGraphicsExtractor, room: DungeonRoom) {
    val color = roomTypeColor(room.type)
    graphics.fillRoom(room, if (room.isViewable) color.rgba else color.darker(0.5f).rgba);
}

object DoorScanner {

    init {
        TickTask(20) {
            if (!SillyMap.enabled) return@TickTask


            for (tile in DungeonScan.tiles) {
                scanDoorsForTile(tile.position)
            }

        }
    }

    fun doorWorldPosition(tile: IVec2, dx: Int, dz: Int): IVec2 =
        IVec2(
            x = (tile.x - 6) * 32 + 7 + dx * 16,
            z = (tile.z - 6) * 32 + 7 + dz * 16
        )

    private fun addDoor(position: IVec2, rotation: DoorRotation, type: DoorType = DoorType.Normal, color: Color = Colors.WHITE) {
        DungeonScan.doors.getOrPut(
            IVec2(
                -12 + 2 * position.x + rotation.offset.x,
                -12 + 2 * position.z + rotation.offset.z
            )
        ) {
            DungeonDoor(position, rotation, type, color)
        }.type = type
    }

    fun allBlocksMatch(
        level: ClientLevel,
        min: BlockPos,
        max: BlockPos
    ): Block? {
        val positions = BlockPos.betweenClosed(min, max).iterator()
        if (!positions.hasNext()) return Blocks.AIR

        val expected = level.getBlockState(positions.next())

        while (positions.hasNext()) {
            val next = positions.next()
            if (level.getBlockState(next) != expected) {
                return null
            }
        }

        return expected.block
    }

    fun scanDoorsForTile(tilePos: IVec2) {
        if (tilePos.x !in 0..5 || tilePos.z !in 0..5) return

        val level = mc.level ?: return
        val tile = DungeonScan.tiles[tilePos.x + tilePos.z * 6]
        val room = tile.room ?: return

        for ((dx, dz) in listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)) {
            val neighborX = tilePos.x + dx
            val neighborZ = tilePos.z + dz
            if (neighborX !in 0..5 || neighborZ !in 0..5) continue

            val neighbor = DungeonScan.tiles[neighborX + neighborZ * 6]
            if (neighbor.room == null || neighbor.room == room) continue

            val (origin, doorRotation) = when {
                dx == 1 -> tilePos to DoorRotation.Horizontal
                dx == -1 -> neighbor.position to DoorRotation.Horizontal
                dz == 1 -> tilePos to DoorRotation.Vertical
                dz == -1 -> neighbor.position to DoorRotation.Vertical
                else -> continue
            }

            val candidatePos = doorWorldPosition(
                origin,
                doorRotation.offset.x,
                doorRotation.offset.z
            )

            val (floorBlock, aboveBlock) = (68..69).map { y ->
                level.getBlockState(BlockPos(candidatePos.x, y, candidatePos.z))
            }
            if (floorBlock.isAir) continue

            val sameBlock = allBlocksMatch(
                level,
                BlockPos(candidatePos.x - 1, 69, candidatePos.z - 1),
                BlockPos(candidatePos.x + 1, 72, candidatePos.z + 1)
            ) ?: continue

            val type = when (sameBlock) {
                Blocks.COAL_BLOCK -> DoorType.Wither
                Blocks.RED_TERRACOTTA -> DoorType.Blood
                Blocks.CHISELED_STONE_BRICKS, Blocks.AIR -> DoorType.Normal
                else -> continue
            }

            val color = when (type) {
                DoorType.Wither -> DungeonMap.witherDoorColor
                DoorType.Blood -> DungeonMap.bloodDoorColor
                else -> DungeonMap.normalDoorColor
            }

            addDoor(origin, doorRotation, type, color)
        }
    }
}