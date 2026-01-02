package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.GuiEvent
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.inventory.ClickType

class SlotInteractEvent(
    screen: Screen,
    val slotId: Int,
    val button: Int,
    val clickType: ClickType
    ) : GuiEvent(screen)
