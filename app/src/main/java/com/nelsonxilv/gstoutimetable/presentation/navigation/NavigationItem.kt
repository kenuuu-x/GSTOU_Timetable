package com.nelsonxilv.gstoutimetable.presentation.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.nelsonxilv.gstoutimetable.R
import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem<T : Any>(
    val route: T,
    @param:StringRes val titleResId: Int,
) {

    @Serializable
    data object Today : NavigationItem<Screen.TodayScreen>(
        route = Screen.TodayScreen,
        titleResId = R.string.today
    )

    @Serializable
    data object Tomorrow : NavigationItem<Screen.TomorrowScreen>(
        route = Screen.TomorrowScreen,
        titleResId = R.string.tomorrow
    )

    @Serializable
    data object Week : NavigationItem<Screen.WeekScreen>(
        route = Screen.WeekScreen,
        titleResId = R.string.week
    )

}

fun NavDestination?.isCurrentScreen(screen: NavigationItem<*>) =
    this?.hierarchy?.any {
        it.hasRoute(screen.route::class)
    }
