package com.odtheking.odinaddon.features.impl.skyblock.event

import com.mojang.blaze3d.platform.InputConstants
import com.odtheking.odin.events.core.CancellableEvent

abstract class MouseEvent : CancellableEvent(){
    class Scroll(val horizontal: Double, val vertical: Double) : MouseEvent();

    class Click(val button: Int) : MouseEvent();

    class Release(val button: Int) : MouseEvent();
}