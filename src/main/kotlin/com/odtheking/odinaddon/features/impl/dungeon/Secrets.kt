package com.odtheking.odinaddon.features.impl.dungeon

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.SelectorSetting
import com.odtheking.odin.events.GuiEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.features.impl.floor7.TerminalSolver.startsWithColor
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.equalsOneOf
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

object Secrets : Module(
    name = "Secrets",
    description = "Secret relevant features"
) {
    private val closeChest by BooleanSetting(
        name = "Close Chests",
        false,
        desc = "Automatically closes chests in dungeons."
    )
    private val closeChestOption by SelectorSetting(
        name = "Chest Close Method",
        "Auto",
        listOf("Auto", "Input"),
        desc = "How to close the chest."
    ).withDependency { closeChest }

    val SECRET_REGEX = Regex("§\\d+/\\d+\\sSecrets")
    private var currSecrets = ""

    private val secretHud by HUD("Secret Hud", desc = "Displays the number of secrets in a room.") {
        if (it) textDim("§6Secrets 67/67", 0, 0, Colors.WHITE)
        if (!DungeonUtils.inDungeons && currSecrets.isEmpty() && !it) return@HUD 0 to 0
        textDim(currSecrets, 0, 0)
    }

    init {
        onReceive<ClientboundSystemChatPacket> {
            if (!overlay) return@onReceive
            if (!DungeonUtils.inDungeons) {
                currSecrets = ""
                return@onReceive
            }
            currSecrets = SECRET_REGEX.find(content.string)?.value ?: "";
        }

        on<GuiEvent.Open> {
            if (closeChestOption != 0 || !closeChest || !DungeonUtils.inDungeons || !screen.title.string.equalsOneOf(
                    "Chest",
                    "Large Chest"
                )
            ) return@on
            mc.player?.closeContainer()
        }

        on<GuiEvent.KeyPress> {
            if (closeChestOption != 1 || !closeChest || !DungeonUtils.inDungeons || !screen.title.string.equalsOneOf(
                    "Chest",
                    "Large Chest"
                )
            ) return@on
            mc.player?.closeContainer()
        }

    }
}