package com.odtheking.odinaddon.features.impl.dungeon

import com.odtheking.odin.clickgui.settings.Setting.Companion.withDependency
import com.odtheking.odin.clickgui.settings.impl.DropdownSetting
import com.odtheking.odin.events.ChatMessageEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.features.Module
import com.odtheking.odin.utils.Colors
import com.odtheking.odin.utils.handlers.TickTask
import com.odtheking.odin.utils.render.textDim
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import com.odtheking.odin.utils.texture
import com.odtheking.odinaddon.features.impl.skyblock.event.WorldEvent
import com.odtheking.odinaddon.utils.EntityCollection
import com.odtheking.odinaddon.utils.Timer
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.zombie.Zombie

object BloodRoomAddons : Module(
    name = "Blood Room Addon",
    description = "TBAddon custom blood room features"
) {
    private val watcherFeatures by DropdownSetting("Watcher", false, desc = "Watcher Addons")
    private val campHUD by HUD(
        name = "Blood Camp HUD",
        desc = "Displays various information related to the watcher."
    ) { example ->
        when {
            example -> {
                textDim("§dDialogue: §r0.00s", 0, 0, Colors.WHITE)
                val width = textDim("§dWatcher Move: §r0.00s", 0, 10, Colors.WHITE).first
                textDim("§dCamp: §r0.00s", 0, 20, Colors.WHITE)
                return@HUD width to 30
            }
            bloodTime.real > .0 -> {
                textDim("§dDialogue:§r %.2fs §7(%.2fs)".format(dialogueTime.tick, dialogueTime.tick), 0, 0, Colors.WHITE)
                val width = textDim("§dWatcher Move:§r " + if (moveTime.stoppedNs > 0 || (moveTime.active && watcherEntity != null)) {
                    "%.2fs §7(%.2fs)".format(moveTime.tick, moveTime.real)
                } else {
                    "???s §7(???s)"
                }, 0, 10, Colors.WHITE).first
                textDim("§dCamp:§r %.2fs §7(%.2fs)".format(bloodTime.tick, bloodTime.real), 0, 20, Colors.WHITE)
                return@HUD width to 30
            }
            else -> return@HUD 0 to 0
        }
    }.withDependency { watcherFeatures }

    private const val BLOOD_START = "[BOSS] The Watcher:"
    private const val BLOOD_END = "[BOSS] The Watcher: You have proven yourself. You may pass."
    private const val BLOOD_DIALOGUE = "[BOSS] The Watcher: Let's see how you can handle this."

    private val watcherEntities = EntityCollection({ it is Zombie && it.isInvisible && it.getItemBySlot(EquipmentSlot.HEAD).texture in watcherSkulls })
    private val watcherEntity: Zombie?
        get() = watcherEntities.find { it.removalReason == null } as? Zombie

    private var bloodStarted = false
    private var dialogueSaid = false
    private var awaitingMove = false

    private var bloodTime = Timer()
    private var dialogueTime = Timer()
    private var moveTime = Timer()

    init {
        on<ChatMessageEvent> {
            if (!DungeonUtils.inClear) return@on

            when {
                BLOOD_START in value && !bloodStarted && !dialogueSaid -> onBloodStart()
                BLOOD_DIALOGUE in value -> {
                    dialogueSaid = true
                    awaitingMove = true
                }
                BLOOD_END in value -> onBloodEnd()
            }
        }

        onReceive<ClientboundMoveEntityPacket> {
            if (!DungeonUtils.inClear || !dialogueSaid || !awaitingMove) return@onReceive
            if (xa == 0.toShort() && ya == 0.toShort() && za == 0.toShort()) return@onReceive

            val level = mc.level ?: return@onReceive
            if (watcherEntity != getEntity(level)) return@onReceive

            moveTime.stop()
            awaitingMove = false
        }

        on<WorldEvent.Unload> {
            reset()
        }

        TickTask(1, true) {
            if (!DungeonUtils.inClear) return@TickTask
            if (!bloodStarted) return@TickTask

            bloodTime.tickIf(true)
            dialogueTime.tickIf(!dialogueSaid)
            moveTime.tickIf(!dialogueSaid || awaitingMove)
        }

    }

    private fun onBloodStart() {
        bloodStarted = true
        dialogueSaid = false
        awaitingMove = false

        bloodTime.start()
        dialogueTime.start()
        moveTime.start()
    }

    private fun onBloodEnd() {
        bloodStarted = false
        bloodTime.stop()
    }

    /**
     * Resets all module-applicable values. Currently limited to blood camp feature.
     */
    private fun reset() {
        bloodStarted = false
        dialogueSaid = false
        awaitingMove = false

        bloodTime.reset()
        dialogueTime.reset()
        moveTime.reset()
    }

    private val watcherSkulls = setOf(
        "ewogICJ0aW1lc3RhbXAiIDogMTY5NzMwOTQxNzI1NiwKICAicHJvZmlsZUlkIiA6ICJjYjYxY2U5ODc4ZWI0NDljODA5MzliNWYxNTkwMzE1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJWb2lkZWRUcmFzaDUxODUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2MmI2ZmI0YjhiNTg2ZGM0Y2RmODAzYjA0NDRkOWI0MWQyNDVjZGY2NjhkYWIzOGZhNmMwNjRhZmU4ZTQ2MSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNjM1MjMyMiwKICAicHJvZmlsZUlkIiA6ICI3MmY5MTdjNWQyNDU0OTk0YjlmYzQ1YjVhM2YyMjIzMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGF0X0d1eV9Jc19NZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNzM5ZDdmNGU2NmE3ZGIyZWE2Y2Q0MTRlNGM0YmE0MWRmN2E5MjQ1NWM5ZmM0MmNhYWIwMTQ2NjVjMzY3YWQ1IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNjI5MjgzNiwKICAicHJvZmlsZUlkIiA6ICIzZDIxZTYyMTk2NzQ0Y2QwYjM3NjNkNTU3MWNlNGJlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcl83MUJsYWNrYmlyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZjZlMWU3ZWQzNjU4NmMyZDk4MDU3MDAyYmMxYWRjOTgxZTI4ODlmN2JkN2I1YjM4NTJiYzU1Y2M3ODAyMjA0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTY5NzIzODQ0NjgxMiwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80Y2VjNDAwMDhlMWMzMWMxOTg0ZjRkNjUwYWJiMzQxMGYyMDM3MTE5ZmQ2MjRhZmM5NTM1NjNiNzM1MTVhMDc3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNjAwOTg2NywKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjM3ZGQxOGI1OTgzYTc2N2U1NTZkYzY0NDI0YWY0YjlhYmRiNzVkNGM5ZThiMDk3ODE4YWZiYzQzMWJmMGUwOSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNTkyNDIwNSwKICAicHJvZmlsZUlkIiA6ICIzZDIxZTYyMTk2NzQ0Y2QwYjM3NjNkNTU3MWNlNGJlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcl83MUJsYWNrYmlyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mNWYwZDc4ZmUzOGQxZDdmNzVmMDhjZGNmMmExODU1ZDZkYTAzMzdlMTE0YTNjNjNlM2JmM2M2MThiYzczMmIwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTU4OTU1MDkyNjM2MSwKICAicHJvZmlsZUlkIiA6ICI0ZDcwNDg2ZjUwOTI0ZDMzODZiYmZjOWMxMmJhYjRhZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaXJGYWJpb3pzY2hlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxOTY3ZGI1ZTMxOTk5MTYyNTIwMjE5MDNjZjRlOTk1MmVmN2NlYzIyMGZhYWNhMWJhNzliYWZlNTkzOGJkODAiCiAgICB9CiAgfQp9",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNjIxMjc1NSwKICAicHJvZmlsZUlkIiA6ICI2NGRiNmMwNTliOTk0OTM2YTY0M2QwODEwODE0ZmJkMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVTaWx2ZXJEcmVhbXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZkNjFlODA1NWY2ZWU5N2FiNWI2MTk2YThkN2VjOTgwNzhhYzM3ZTAwMzc2MTU3YjZiNTIwZWFhYTJmOTNhZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
        "ewogICJ0aW1lc3RhbXAiIDogMTcxOTYwNjIzOTU4NiwKICAicHJvZmlsZUlkIiA6ICJhYWZmMDUwYTExOTk0NzM1YjEyNDVlNDk0MGFlZjY4NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMYXN0SW1tb3J0YWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVjMWRjNDdhMDRjZTU3MDAxYThiNzI2ZjAxOGNkZWY0MGI3ZWE5ZDdiZDZkODM1Y2E0OTVhMGVmMTY5Zjg5MyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
    )
}