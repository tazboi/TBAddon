package com.odtheking.odinaddon.features.impl.skyblock.event

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.events.core.CancellableEvent

class KeyboardEvent(val button: InputConstants.Key) : CancellableEvent() {
}