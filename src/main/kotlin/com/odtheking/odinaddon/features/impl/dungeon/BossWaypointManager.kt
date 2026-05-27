package com.odtheking.odinaddon.features.impl.dungeon

import com.odtheking.odin.events.SecretPickupEvent
import net.minecraft.world.level.block.Blocks


//Not currently implemented
object BossWaypointManager {

    fun onLever(event: SecretPickupEvent.Interact) {
        if (event.blockState.block != Blocks.LEVER) return;
    }


}