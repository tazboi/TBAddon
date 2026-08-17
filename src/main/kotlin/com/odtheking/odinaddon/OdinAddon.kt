package com.odtheking.odinaddon

import com.odtheking.odin.config.ModuleConfig
import com.odtheking.odin.events.core.EventBus
import com.odtheking.odin.features.ModuleManager
import com.odtheking.odinaddon.commands.highlightCommand
import com.odtheking.odinaddon.commands.odinAddonCommand
import com.odtheking.odinaddon.commands.protectItemCommand
import com.odtheking.odinaddon.commands.visualWordsCommand
import com.odtheking.odinaddon.features.impl.dungeon.Secrets
import com.odtheking.odinaddon.features.impl.dungeon.MimicChestHighlight
import com.odtheking.odinaddon.features.impl.boss.WitherHighlight
import com.odtheking.odinaddon.features.impl.dungeon.BloodRoomAddons
import com.odtheking.odinaddon.features.impl.dungeon.DungeonMobs
import com.odtheking.odinaddon.features.impl.render.Animations
import com.odtheking.odinaddon.features.impl.render.DamageParsing
import com.odtheking.odinaddon.features.impl.render.ItemColor
import com.odtheking.odinaddon.features.impl.render.PlayerDisplayPlus
import com.odtheking.odinaddon.features.impl.render.RenderModifier
import com.odtheking.odinaddon.features.impl.render.VisualWords
import com.odtheking.odinaddon.features.impl.skyblock.BigInteractables
import com.odtheking.odinaddon.features.impl.skyblock.BowPullback
import com.odtheking.odinaddon.features.impl.skyblock.Click
import com.odtheking.odinaddon.features.impl.skyblock.Cooldowns
import com.odtheking.odinaddon.features.impl.skyblock.Highlight2
import com.odtheking.odinaddon.features.impl.skyblock.ItemHelpers
import com.odtheking.odinaddon.features.impl.skyblock.ItemSwap
import com.odtheking.odinaddon.features.impl.skyblock.LoadoutKeybinds
import com.odtheking.odinaddon.features.impl.skyblock.ProtectItem
import com.odtheking.odinaddon.features.impl.skyblock.Sacks
import com.odtheking.odinaddon.features.impl.skyblock.event.CustomEventDispatcher
import com.odtheking.odinaddon.utils.EntityCache
import com.odtheking.odinaddon.utils.PlayerScheduler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object OdinAddon : ClientModInitializer {

    override fun onInitializeClient() {
        println("Odin Addon initialized!")

        // Register commands by adding to the array
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            arrayOf(odinAddonCommand, highlightCommand, protectItemCommand, visualWordsCommand).forEach { commodore -> commodore.register(dispatcher) }
        }

        // Register objects to event bus by adding to the list
        listOf(this, CustomEventDispatcher, EntityCache, PlayerScheduler).forEach { EventBus.subscribe(it) }

        // Register modules by adding to the function
        ModuleManager.registerModules(
            ModuleConfig("UC30.json"), BowPullback,
            ItemColor, Click, WitherHighlight, MimicChestHighlight, Highlight2, ProtectItem, Secrets, Animations,
            RenderModifier, PlayerDisplayPlus, VisualWords, ItemSwap, Sacks, Cooldowns, ItemHelpers, DungeonMobs,
            BigInteractables, DamageParsing, BloodRoomAddons, LoadoutKeybinds
        )
    }
}
