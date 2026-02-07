package com.odtheking.odinaddon.features.impl.skyblock.event

import com.odtheking.odin.events.core.Event

abstract class InputPassEvent : Event() {
    class HotbarKeys() : InputPassEvent()
}