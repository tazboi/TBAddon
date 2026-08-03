package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.CancellableEvent
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState

class BlockOutlineEvent(val context: LevelRenderContext, val blockOutline: BlockOutlineRenderState) : CancellableEvent() {
}