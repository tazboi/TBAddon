package com.odtheking.odinaddon.features.impl.skyblock.event
import com.odtheking.odin.events.core.Event
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.Entity

abstract class EntityWorldEvent(val entity: Entity, val world: ClientLevel) : Event(){
    class Join(entity: Entity, world: ClientLevel) : EntityWorldEvent(entity, world);

    class Leave(entity: Entity, world: ClientLevel) : EntityWorldEvent(entity, world);
}