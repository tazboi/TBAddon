package com.odtheking.odinaddon

import com.odtheking.odin.features.ModuleManager
import net.fabricmc.api.ClientModInitializer

object OdinAddon : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon initialized!")
        println(ModuleManager.modules.map { it.name })
    }
}
