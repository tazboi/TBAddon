package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.CancellableEvent
import com.odtheking.odin.events.core.Event
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack

class HotbarSlotRenderEvent(val guiGraphics: GuiGraphics, val x: Int, val y: Int, val item: ItemStack) : CancellableEvent() {
}