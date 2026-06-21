package com.odtheking.odinaddon.utils

class Timer {
    private var startNs = 0L
    var stoppedNs = 0L

    var tick: Double = 0.0
        private set

    val real: Double
        get() {
            if (startNs == 0L) return 0.0

            val end = if (stoppedNs != 0L) stoppedNs else System.nanoTime()
            return (end - startNs) / 1_000_000_000.0
        }

    val active: Boolean
        get() = startNs != 0L && stoppedNs == 0L

    fun start() {
        startNs = System.nanoTime()
        stoppedNs = 0L
        tick = 0.0
    }

    fun stop() {
        if (!active) return

        stoppedNs = System.nanoTime()
    }

    fun reset() {
        startNs = 0L
        stoppedNs = 0L
        tick = 0.0
    }

    fun tickIf(predicate: Boolean) {
        if (!active) return

        if (!predicate) {
            stop()
            return
        }

        tick += 0.05
    }

}