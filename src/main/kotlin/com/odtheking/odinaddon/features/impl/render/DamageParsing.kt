package com.odtheking.odinaddon.features.impl.render

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.ColorSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.modMessage
import com.odtheking.odinaddon.features.impl.skyblock.event.EntityWorldEvent
import com.odtheking.odinaddon.utils.removeUnicode
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DamageParsing : Module(
    name = "Damage Parser",
    description = "Modules that interact with chat/send messages to chat."
) {
    private val timestamped by BooleanSetting("Timestamps", false, desc = "Timestamps messages sent by this module.")
    private val timestampColor by ColorSetting("Timestamp Color", Colors.MINECRAFT_AQUA, desc = "Timestamp color").withDependency { timestamped }
    private val damageTracker by BooleanSetting("Damage Numbers", false, desc = "Parses damage numbers on-screen to your chat. Does not guarantee you as the source of damage.")
    private val removeNonCrits by BooleanSetting("Remove Non-Crits", false, "Removes non-critical hit ArmorStand entities from staying in-world.")
    private val DAMAGE_REGEX = Regex("""\b(?:\d+|\d{1,3}(?:,\d{3})+)\b""")
    private val CRIT_REGEX = Regex("""✧.*?\d+.*?✧""")

    init {
        on<EntityWorldEvent.Join> {
            parseDamageToChat(entity)
            removeNonCrit(this)
        }
    }

    private fun parseDamageToChat(entity: Entity) {
        if (!damageTracker || entity !is ArmorStand) return

        val name = entity.customName ?: return
        if (!name.string.removeUnicode().matches(DAMAGE_REGEX)) return
        val timeStamp = millisToMilitaryTime()
        modMessage(
            Component.literal(if (timestamped) "[$timeStamp]: " else "").withColor(timestampColor.rgba)
                .append(name)
        )
    }

    private fun removeNonCrit(event: EntityWorldEvent.Join) {
        if (!removeNonCrits || event.entity !is ArmorStand) return
        val name = event.entity.customName ?: return

        if (!name.string.removeUnicode().matches(DAMAGE_REGEX)) return
        if (name.string.matches(CRIT_REGEX)) return

        event.world.removeEntity(event.entity.id, Entity.RemovalReason.DISCARDED)
    }

    private fun millisToMilitaryTime(
        millis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        return Instant.ofEpochMilli(millis)
            .atZone(zoneId)
            .format(DateTimeFormatter.ofPattern("HH:mm:ss.SS"))
    }
}