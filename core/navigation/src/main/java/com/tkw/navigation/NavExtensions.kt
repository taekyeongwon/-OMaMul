package com.tkw.navigation

import android.content.Context
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions

fun NavController.deepLinkNavigateTo(
    context: Context,
    deepLinkDestination: DeepLinkDestination,
    popupTo: Boolean = false
) {
    val builder = NavOptions.Builder()

    if(popupTo) {
        builder.setPopUpTo(graph.startDestinationId, true)
    }

    navigate(
        buildDeepLink(context, deepLinkDestination),
        builder.build()
    )
}

private fun buildDeepLink(context: Context, destination: DeepLinkDestination) =
    NavDeepLinkRequest.Builder
        .fromUri(destination.getDeepLink(context).toUri())
        .build()