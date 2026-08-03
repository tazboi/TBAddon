package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.CancellableEvent
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.item.ItemStack

class HotbarSlotRenderEvent(val guiGraphics: GuiGraphicsExtractor, val x: Int, val y: Int, val item: ItemStack) : CancellableEvent() {
}