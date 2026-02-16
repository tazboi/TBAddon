package com.odtheking.odinaddon.features.impl.skyblock

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.handlers.TickTask
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.LocationUtils
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odin.utils.toFixed
import com.odtheking.odinaddon.features.impl.skyblock.event.WorldEvent
import com.odtheking.odinaddon.utils.getColor
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.sounds.SoundEvents
import kotlin.math.ceil

object Cooldowns : Module (
    name = "Cooldowns",
    description = "Tracks common cooldowns in Skyblock."
) {
    private val witherImpact by HUD(
        name = "Wither Impact HUD",
        desc = "Displays number of ticks until Wither Impact is off cooldown."
    ) { example ->
        if (example) textDim("§6Shield: §c5.00s", 0, 0, Colors.WHITE)
        else if (witherImpactTicks > 0 && LocationUtils.isInSkyblock) {
            textDim(witherImpactText, 0, 0)
        }
        else return@HUD 0 to 0
    }

    private val compact by BooleanSetting("Compact Text", false, desc = "Shortens cooldown text.")
    private var witherImpactTicks: Int = -1;

    init {
        TickTask(1, true) {
            if (witherImpactTicks > 0) witherImpactTicks--
        }

        onReceive<ClientboundSoundPacket> {
            when {
                sound.value() == SoundEvents.ZOMBIE_VILLAGER_CURE && pitch == 0.6984127f && volume == 1f -> witherImpactTicks = 100
            }
        }

        on<WorldEvent.Load> {
            witherImpactTicks = 0
        }
    }

    private inline val witherImpactText: String get() =
        if (compact) if (witherImpactTicks <= 0) "" else "${getColor(witherImpactTicks, 100, true)}${(witherImpactTicks / 20f).toFixed()}"
        else if (witherImpactTicks <= 0) "§6Shield: §aReady" else "§6Shield: ${getColor(witherImpactTicks, 100, true)}${(witherImpactTicks / 20f).toFixed()}s"
}