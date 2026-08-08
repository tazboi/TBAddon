package com.odtheking.odinaddon.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.utils.Color
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.features.impl.skyblock.Highlight2
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

val addRegex = Regex("""^(.+?)(?:\s+(#?[0-9A-Fa-f]{6}))?$""")
val highlightCommand = Commodore("highlight", "hl") {

    literal("add", "a").runs { input: GreedyString ->
        val trimmed = input.string.trim()

        val match = addRegex.matchEntire(trimmed)
        val mobName = match?.groupValues?.get(1)?.trim() ?: return@runs modMessage("Invalid format. Use: /highlight add <mobname> [#hex]")
        if (Highlight2.highlightMap.containsKey(mobName)) return@runs modMessage("$mobName already exists in the highlight list.")

        val color = match.groupValues[2].takeIf { it.trim().isNotEmpty()}?.removePrefix("#")?.toInt(16)?.let(::Color)
            ?: Highlight2.defaultColor
        Highlight2.highlightMap[mobName] = color
        modMessage("${mobName} added to highlight list with color ${if (color == Highlight2.defaultColor) "default" else color.hex()}.")
        ModuleManager.saveConfigurations()
    }


    literal("remove", "r", "d", "del").runs { name: GreedyString ->
        val lowerTrimmed = name.string.trim().lowercase()
        if (lowerTrimmed.isEmpty()) return@runs modMessage("Name cannot be empty.")

        val toRemove =
            Highlight2.highlightMap.keys.find { it.lowercase().contains(lowerTrimmed) } ?: return@runs modMessage(
                "$name was not found in the highlight list."
            )
        Highlight2.highlightMap.remove(toRemove)
        modMessage("$toRemove was successfully removed from the highlight list.")
        ModuleManager.saveConfigurations()
    }

    literal("list", "l").runs {
        if (Highlight2.highlightMap.isEmpty()) return@runs modMessage("Highlight list is empty.")

        Highlight2.highlightMap.forEach { (mob, color) ->
            val line = Component.literal("- $mob ")
                .append(
                    Component.literal("§c[Remove]")
                        .withStyle {
                            it.withHoverEvent(
                                HoverEvent.ShowText(
                                    Component.literal("§eClick to remove §f$mob§e from highlights.")
                                )
                            ).withClickEvent(
                                ClickEvent.RunCommand("/hl r $mob")
                            )
                        }
                )
            modMessage(line)
        }
    }
}
