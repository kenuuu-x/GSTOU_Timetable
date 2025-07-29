package com.nelsonxilv.gstoutimetable.presentation.screens.main

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.DateType
import com.nelsonxilv.gstoutimetable.presentation.components.FirstRunDialog
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableAppBar
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableNavBarItem
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableNavigationBar
import com.nelsonxilv.gstoutimetable.presentation.components.content.FullSearchBarContent
import com.nelsonxilv.gstoutimetable.presentation.navigation.AppNavGraph
import com.nelsonxilv.gstoutimetable.presentation.navigation.NavigationItem
import com.nelsonxilv.gstoutimetable.presentation.navigation.NavigationState
import com.nelsonxilv.gstoutimetable.presentation.navigation.isCurrentScreen
import com.nelsonxilv.gstoutimetable.presentation.navigation.rememberNavigationState
import com.nelsonxilv.gstoutimetable.presentation.screens.main.contract.TimetableUiEvent
import com.nelsonxilv.gstoutimetable.presentation.screens.main.contract.TimetableUiState
import com.nelsonxilv.gstoutimetable.presentation.screens.singleday.TimetableDayScreen
import com.nelsonxilv.gstoutimetable.presentation.screens.week.WeekScreen

private const val SharedContentStateKey = "shared_content_state"
private const val TimetablePreferences = "timetable_preferences"
private const val IsFirstLaunchKey = "isFirstLaunch"

@Composable
fun TimetableApp() {
    val viewModel = hiltViewModel<TimetableViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    TimetableScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun TimetableScreen(
    uiState: TimetableUiState = TimetableUiState(),
    onEvent: (TimetableUiEvent) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    val navigationState = rememberNavigationState()

    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(TimetablePreferences, Context.MODE_PRIVATE)
    }
    val isFirstLaunchDialog = remember {
        mutableStateOf(sharedPreferences.getBoolean(IsFirstLaunchKey, true))
    }
    val showDialog = remember { mutableStateOf(isFirstLaunchDialog.value) }

    LaunchedEffect(Unit) {
        onEvent(TimetableUiEvent.OnDataUpdate)
    }

    BackHandler(enabled = isSearchVisible) {
        isSearchVisible = false
    }

    if (showDialog.value) {
        FirstRunDialog(
            onConfirmation = {
                showDialog.value = false
                if (isFirstLaunchDialog.value) {
                    sharedPreferences.edit { putBoolean(IsFirstLaunchKey, false) }
                    isFirstLaunchDialog.value = false
                }
            }
        )
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = isSearchVisible,
            label = "search_visibility",
        ) { searchVisible ->
            when (searchVisible) {
                true -> {
                    FullSearchBarContent(
                        modifier = Modifier.fillMaxSize(),
                        savedGroupList = uiState.savedGroupList,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        onSearch = { groupName ->
                            onEvent(TimetableUiEvent.OnGroupSearchClick(groupName))
                        },
                        onActiveChanged = { isSearchVisible = it },
                        onGroupItemClick = { groupName ->
                            isSearchVisible = false
                            onEvent(TimetableUiEvent.OnGroupSearchClick(groupName))
                        },
                        onClearIconButtonClick = { groupName ->
                            onEvent(TimetableUiEvent.OnDeleteGroupClick(groupName))
                            if (groupName == uiState.currentGroupName) {
                                navigationState.navigateTo(NavigationItem.Today)
                            }
                        }
                    )
                }

                false -> {
                    TimetableContent(
                        uiState = uiState,
                        scrollBehavior = scrollBehavior,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        navigationState = navigationState,
                        isSearchVisible = isSearchVisible,
                        changeSearchVisibility = { isSearchVisible = it },
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun TimetableContent(
    uiState: TimetableUiState,
    scrollBehavior: TopAppBarScrollBehavior,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navigationState: NavigationState,
    isSearchVisible: Boolean,
    changeSearchVisibility: (Boolean) -> Unit,
    onEvent: (TimetableUiEvent) -> Unit
) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TimetableAppBar(
                scrollBehavior = scrollBehavior,
                titleAppBar = uiState.currentGroupName
                    ?: stringResource(id = R.string.full_app_name),
                isDataUpdating = uiState.isDataUpdating,
                onUpdateButtonClick = { onEvent(TimetableUiEvent.OnDataUpdate) }
            )
        },
        floatingActionButton = {
            with(sharedTransitionScope) {
                FloatingActionButton(
                    onClick = { changeSearchVisibility(true) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = SharedContentStateKey),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !isSearchVisible && uiState.currentGroupName != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val items = listOf(
                    NavigationItem.Today,
                    NavigationItem.Tomorrow,
                    NavigationItem.Week
                )
                val navBackStackEntry by navigationState
                    .navHostController
                    .currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val selectedItem = rememberSaveable(currentDestination) {
                    derivedStateOf {
                        items.indexOfFirst { screen ->
                            currentDestination?.isCurrentScreen(screen) == true
                        }.coerceAtLeast(0)
                    }.value
                }

                TimetableNavigationBar(
                    itemsListSize = items.size,
                    selectedItemIndex = selectedItem
                ) {
                    items.forEach { screen ->
                        val isSelected =
                            currentDestination?.isCurrentScreen(screen) == true

                        TimetableNavBarItem(
                            text = stringResource(screen.titleResId),
                            selected = isSelected,
                            onClick = { navigationState.navigateTo(screen) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navHostController = navigationState.navHostController,
            todayScreenContent = {
                TimetableDayScreen(
                    searchGroupName = uiState.currentGroupName,
                    dateType = DateType.TODAY,
                    contentPadding = innerPadding,
                    onCardClick = { changeSearchVisibility(true) },
                )
            },
            tomorrowScreenContent = {
                TimetableDayScreen(
                    searchGroupName = uiState.currentGroupName,
                    dateType = DateType.TOMORROW,
                    contentPadding = innerPadding,
                    onCardClick = { changeSearchVisibility(true) },
                )
            },
            weekScreenContent = {
                WeekScreen(
                    searchGroupName = uiState.currentGroupName,
                    contentPadding = innerPadding,
                    onCardClick = { changeSearchVisibility(true) }
                )
            }
        )
    }
}