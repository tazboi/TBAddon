package com.odtheking.odinaddon.utils

import com.odtheking.odin.OdinMod
import net.minecraft.core.registries.Registries
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket

val ClientboundLevelParticlesPacket.name: String?
    get() {
        val registry = OdinMod.mc.level?.registryAccess()?.lookupOrThrow(Registries.PARTICLE_TYPE) ?: return null
        return registry.getKey((this.particle.type))?.toString()
    }