package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.CancellableEvent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult

class UseItemOnEvent(val item: ItemStack, val hitResult: BlockHitResult) : CancellableEvent() {
}