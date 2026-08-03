package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.OdinMod
import com.odtheking.odin.events.core.onReceive
import com.odtheking.odin.utils.handlers.schedule
import com.odtheking.odin.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket

object CustomEventDispatcher {
    init {
        onReceive<ClientboundPlayerPositionPacket> {
            if (!DungeonUtils.inDungeons || !DungeonUtils.inBoss) return@onReceive;
            EnterDungeonBossEvent(DungeonUtils.floor).postAndCatch()
        }

        onReceive<ClientboundAddEntityPacket> {
            schedule(1) {
                val world = OdinMod.mc.level ?: return@schedule
                val entity = world.getEntity(id) ?: world.getEntity(uuid) ?: return@schedule

                EntityWorldEvent.Join(entity, world).postAndCatch()
            }
        }

        ClientEntityEvents.ENTITY_LOAD.register { entity, world ->
            EntityWorldEvent.Join(entity, world).postAndCatch()
        }

        ClientEntityEvents.ENTITY_UNLOAD.register { entity, world ->
            EntityWorldEvent.Leave(entity, world).postAndCatch()
        }

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register { context, blockOutline ->
            !BlockOutlineEvent(context, blockOutline).postAndCatch()
        }

        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            WorldEvent.Load().postAndCatch()
        }

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register { _, _ ->
            WorldEvent.Unload().postAndCatch()
        }
    }
}