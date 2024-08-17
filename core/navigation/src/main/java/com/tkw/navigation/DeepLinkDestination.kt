package com.tkw.navigation

import android.content.Context

sealed class DeepLinkDestination(val addressRes: Int) {
    object Home: DeepLinkDestination(R.string.home_deeplink)
    object Cup: DeepLinkDestination(R.string.cup_manage_deeplink)
    object Alarm: DeepLinkDestination(R.string.alarm_deeplink)
}

fun DeepLinkDestination.getDeepLink(context: Context) = context.getString(this.addressRes)