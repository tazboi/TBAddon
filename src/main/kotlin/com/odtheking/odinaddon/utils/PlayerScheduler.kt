package com.odtheking.odinaddon.utils

import com.odtheking.odin.OdinMod.mc
import com.odtheking.odin.utils.handlers.TickTask
import com.odtheking.odin.utils.handlers.TickTasks

object PlayerScheduler {
    var swapTasks = mutableMapOf<TickTask, Int>()

    fun scheduleSwap(index: Int, ticks: Int = 1, serverTick: Boolean = false) {
        if (ticks < 1) return
        val delay = (swapTasks.values.lastOrNull() ?: 1) + ticks //Always swap after last one scheduled

        lateinit var tickTask: TickTask //Copied from @TickTask
        tickTask = object : TickTask(delay, serverTick, {
            (mc.player?.inventory?.selectedSlot != index).let {
                mc.player?.inventory?.setSelectedSlot(index.coerceIn(0, 8)) //Setter has extra checks, not sure if kotlin is idiomatic enough to make sure those go through.
            }
            swapTasks.remove(tickTask)
            TickTasks.unregister(tickTask)
        }) {}
        swapTasks[tickTask] = delay
    }

}