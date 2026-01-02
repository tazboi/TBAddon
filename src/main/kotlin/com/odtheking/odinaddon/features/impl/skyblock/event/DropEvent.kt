package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.CancellableEvent
import net.minecraft.world.item.ItemStack

class DropEvent(val item: ItemStack?, val all: Boolean = false) : CancellableEvent() {
}