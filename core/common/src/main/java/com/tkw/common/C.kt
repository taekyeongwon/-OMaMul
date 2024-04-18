package com.tkw.common

object C {
    val FirstInstallFlag = "firstInstallFlag"
    enum class CupViewType(val viewType: Int) {
        CUP(0), ADD(1)
    }

    enum class CupListViewType(val viewType: Int) {
        NORMAL(0), DRAG(1)
    }
}