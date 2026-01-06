package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.MapSetting
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.modMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object VisualWords : Module(
    name = "Visual Words",
    description = "Visually replaces words on screen"
) {
    val visualWordMap = this.registerSetting(MapSetting("Visual Words Map", mutableMapOf<String, String>())).value

    fun modify(original: FormattedCharSequence): FormattedCharSequence {
        val styled = original.styledChars
        val plain = styled.joinToString("") { it.char.toString() }

        val entry = visualWordMap.entries.firstOrNull { plain.contains(it.key) }
            ?: return original

        val (from, to) = entry
        val start = plain.indexOf(from)
        val result = mutableListOf<StyledChar>()

        result.addAll(styled.subList(0, start))
        result.addAll(to.toStyledChars())
        result.addAll(styled.subList(start + from.length, styled.size))

        return FormattedCharSequence { consumer ->
            result.forEachIndexed { i, sc ->
                if (!consumer.accept(i, sc.style, sc.char.code)) return@FormattedCharSequence false
            }
            true
        }
    }

    inline val FormattedCharSequence.styledChars: List<StyledChar>
        get() {
            val list = mutableListOf<StyledChar>()
            this.accept { _, style, codepoint ->
                list.add(StyledChar(codepoint.toChar(), style))
                true
            }
            return list
        }

    fun String.toStyledChars(): List<StyledChar> {
        val fcs = Component.literal(this.replace("&", "§")).visualOrderText
        return fcs.styledChars
    }

    data class StyledChar(val char: Char, val style: Style)
}