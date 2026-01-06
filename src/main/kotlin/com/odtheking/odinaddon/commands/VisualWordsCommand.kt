package com.odtheking.odinaddon.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.features.impl.render.VisualWords
import com.odtheking.odinaddon.features.impl.skyblock.Highlight2
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import kotlin.collections.component1
import kotlin.collections.component2

val visualWordsCommand = Commodore("visualwords", "vw") {
    val replaceRegex = Regex(""""([^"]+)"\s+replace\s+"([^"]+)"""")
    val removeRegex = Regex("""(?<=")[^"]*(?=")""")
    runs {
        modMessage("Invalid Syntax! (quotations necessary) Use /vw add \"First Phrase\" replace \"Second Phrase\"")
    }

    literal("add", "a").runs { input: GreedyString ->
        val match = replaceRegex.find(input.string)
            ?: return@runs modMessage("Invalid Syntax! (quotations necessary) Use /vw add \"First Phrase\" replace \"Second Phrase\"")
        val (g1, g2) = match.destructured
        VisualWords.visualWordMap[g1]?.let {
            return@runs modMessage("This Visual Word already exists! It is mapped to ${VisualWords.visualWordMap[g1]}.")
        }

        VisualWords.visualWordMap[g1] = g2;
        modMessage("$g1 has been set to $g2 in Visual Words.")
        ModuleManager.saveConfigurations()
    }

    literal("remove", "r").runs { input: GreedyString ->
        val match = removeRegex.find(input.string.trim())
            ?: return@runs modMessage("Invalid syntax! (quotations necessary) Use /vw r \"Phrase\"")
        VisualWords.visualWordMap[match.value]
            ?: return@runs modMessage("${match.value} does not exist in Visual Words!")

        VisualWords.visualWordMap.remove(match.value)
        modMessage("${match.value} successfully removed from Visual Words.")
        ModuleManager.saveConfigurations()
    }

    literal("list", "l").runs {
        VisualWords.visualWordMap.forEach { (word1, word2) ->
            val line = Component.literal("- $word1 --> $word2 ")
                .append(
                    Component.literal("§c[Remove]")
                        .withStyle {
                            it.withHoverEvent(
                                HoverEvent.ShowText(
                                    Component.literal("§eClick to remove §f$word1§e from Visual Words.")
                                )
                            ).withClickEvent(
                                ClickEvent.RunCommand("/vw r \"$word1\"")
                            )
                        }
                )
            modMessage(line)
        }
    }
}