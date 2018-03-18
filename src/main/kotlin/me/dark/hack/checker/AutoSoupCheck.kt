package me.dark.hack.checker

class AutoSoupCheck {

    private var lastHeld: Long = 0
    var lastInform: Long = 0
        private set
    private var lastInteract: Long = 0

    private var preClick: Long = 0
    private var lastClick: Long = 0

    private var lastClose: Long = 0
    private var fails: Int = 0

    init {
        lastHeld = java.lang.Long.MAX_VALUE
        lastInform = java.lang.Long.MIN_VALUE
        lastInteract = java.lang.Long.MAX_VALUE
        preClick = java.lang.Long.MAX_VALUE
        lastClick = java.lang.Long.MAX_VALUE
        lastClose = java.lang.Long.MAX_VALUE
        fails = 0
    }

    fun setHeld() {
        lastHeld = System.currentTimeMillis()
    }

    fun setLastInteract() {
        lastInteract = System.currentTimeMillis()
    }

    fun setLastClick() {
        preClick = lastClick
        lastClick = System.currentTimeMillis()
        lastClose = java.lang.Long.MAX_VALUE
    }

    fun setLastClose() {
        lastClose = System.currentTimeMillis()
    }

    fun isLegit(moment: CheckMoment): Boolean {
        if (moment == CheckMoment.INTERACT || moment == CheckMoment.HELD) {
            if (lastClose == java.lang.Long.MAX_VALUE && lastClick != java.lang.Long.MAX_VALUE) {
                fails += 1
                return fails < 2
            }
            if (lastClose != java.lang.Long.MAX_VALUE && lastClose > lastClick &&
                    lastClose - lastClick < 10L) {
                fails += 1
                return fails < 2
            }
        }

        if (moment == CheckMoment.INTERACT) {
            if (lastHeld != java.lang.Long.MAX_VALUE && lastInteract - lastHeld < 5L) {
                fails += 1
                return fails < 2
            }


            if (preClick != java.lang.Long.MAX_VALUE && lastClick - preClick < 20L) {
                fails += 1
                return fails < 2
            }
        }
        return fails < 2
    }

    enum class CheckMoment constructor() {
        CLICK, HELD, INTERACT
    }
}