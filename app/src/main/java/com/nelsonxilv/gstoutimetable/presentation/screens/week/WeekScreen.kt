package com.nelsonxilv.gstoutimetable.presentation.screens.week

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.Day
import com.nelsonxilv.gstoutimetable.domain.entity.DayOfWeek
import com.nelsonxilv.gstoutimetable.domain.entity.Lesson
import com.nelsonxilv.gstoutimetable.domain.entity.TimeInterval
import com.nelsonxilv.gstoutimetable.presentation.components.DayItem
import com.nelsonxilv.gstoutimetable.presentation.components.SubgroupFilterChips
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableNavBarItem
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableNavigationBar
import com.nelsonxilv.gstoutimetable.presentation.components.content.ContentContainer
import com.nelsonxilv.gstoutimetable.presentation.components.content.ContentContainerOption
import com.nelsonxilv.gstoutimetable.presentation.navigation.NavigationItem
import com.nelsonxilv.gstoutimetable.presentation.screens.week.contract.WeekUiEvent
import com.nelsonxilv.gstoutimetable.presentation.screens.week.contract.WeekUiState
import com.nelsonxilv.gstoutimetable.presentation.theme.GSTOUTimetableTheme

@Composable
fun WeekScreen(
    searchGroupName: String?,
    contentPadding: PaddingValues,
    onCardClick: () -> Unit,
) {
    val viewModel = hiltViewModel<WeekViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    WeekContent(
        uiState = uiState,
        searchGroupName = searchGroupName ?: "",
        contentPadding = contentPadding,
        onEvent = viewModel::handleEvent,
        onCardClick = onCardClick,
        onCopied = {
            Toast.makeText(
                context,
                R.string.text_copied,
                Toast.LENGTH_SHORT
            ).show()
        }
    )

}

@Composable
fun WeekContent(
    uiState: WeekUiState = WeekUiState(),
    searchGroupName: String = "",
    contentPadding: PaddingValues = PaddingValues(),
    onEvent: (WeekUiEvent) -> Unit = {},
    onCardClick: () -> Unit = {},
    onCopied: () -> Unit = {},
) {

    LaunchedEffect(searchGroupName) {
        onEvent(WeekUiEvent.OnGroupSearch(searchGroupName))
    }

    AnimatedContent(
        targetState = uiState,
        label = "Animated content"
    ) { targetState ->
        when {
            targetState.isLoading -> ContentContainer(isLoading = true)

            targetState.isEmptyLessonList -> ContentContainer(
                option = ContentContainerOption.EmptyLessons(),
                onCardClick = onCardClick,
                modifier = Modifier.padding(contentPadding)
            )

            targetState.isLoadingLessonsError -> ContentContainer(
                option = ContentContainerOption.LoadingError(
                    errorMessage = targetState.errorMessage,
                    onCopied = onCopied
                ),
                onCardClick = onCardClick,
                modifier = Modifier.padding(contentPadding)
            )

            targetState.daysWithFilteredLessons.isNotEmpty() -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    WeekFilterBar(
                        currentWeekNum = targetState.selectedWeekNumber,
                        showFilterChips = targetState.showFilterChips,
                        itemMenuClick = {
                            onEvent(WeekUiEvent.OnItemMenuClick(it))
                        },
                        onFilterChipClick = {
                            onEvent(WeekUiEvent.OnSubgroupChipClick(it))
                        }
                    )
                }

                items(
                    items = uiState.daysWithFilteredLessons,
                    key = { it.name }
                ) { day -> DayItem(day) }
            }
        }
    }
}

@Composable
private fun WeekFilterBar(
    currentWeekNum: Int = 1,
    showFilterChips: Boolean = false,
    itemMenuClick: (Int) -> Unit = {},
    onFilterChipClick: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
    ) {
        var expanded by remember { mutableStateOf(false) }

        FilterChip(
            onClick = { expanded = !expanded },
            label = { Text(text = stringResource(R.string.number_week, currentWeekNum)) },
            selected = false,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                )
            },
            modifier = Modifier
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.number_week, 1)) },
                onClick = { itemMenuClick(1) }
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.number_week, 2)) },
                onClick = { itemMenuClick(2) }
            )
        }

        if (showFilterChips) {
            SubgroupFilterChips(
                selectedSubgroupNumber = 1,
                onFilterChipClick = onFilterChipClick
            )
        }
    }
}

@Composable
private fun BasedWeekScreenPreview() {
    GSTOUTimetableTheme {
        val lesson = Lesson(
            lessonId = 1,
            name = "Программирование",
            teacher = "Иванов И.И.",
            auditorium = "301",
            groupNames = listOf("ИВТ-21", "ПИ-21"),
            timeInterval = TimeInterval("9:00", "10:20"),
            activityType = ActivityType.LECTURE,
            period = 1,
            dayOfWeek = DayOfWeek.MONDAY,
            week = 1,
            subgroupNumber = 0
        )
        val lesson2 = Lesson(
            lessonId = 2,
            name = "История",
            teacher = "Иванов И.И.",
            auditorium = "302",
            groupNames = listOf("ИВТ-21"),
            timeInterval = TimeInterval("10:30", "11:50"),
            activityType = ActivityType.PRACTICE,
            period = 2,
            dayOfWeek = DayOfWeek.MONDAY,
            week = 1,
            subgroupNumber = 1
        )

        val day = Day(
            name = "Понедельник",
            lessons = listOf(lesson, lesson2)
        )

        val listDay = listOf(
            day,
            day.copy(name = "Вторник"),
            day.copy(name = "Среда"),
            day.copy(name = "Четверг"),
            day.copy(name = "Пятница"),
            day.copy(name = "Суббота"),
            day.copy(name = "Воскресенье", lessons = emptyList()),
        )
        val weekUiState = WeekUiState(days = listDay, daysWithFilteredLessons = listDay)
        Scaffold(
            bottomBar = {
                var selectedItem by remember { mutableIntStateOf(0) }
                val items = listOf(
                    NavigationItem.Today,
                    NavigationItem.Tomorrow,
                )

                TimetableNavigationBar(
                    itemsListSize = items.size,
                    selectedItemIndex = selectedItem
                ) {
                    items.forEachIndexed { index, item ->
                        TimetableNavBarItem(
                            text = stringResource(item.titleResId),
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            WeekContent(uiState = weekUiState, contentPadding = paddingValues)
        }
    }
}

@Preview(
    name = "light_mode",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true,
    locale = "ru"
)
@Composable
private fun WeekScreenLightPreview() {
    BasedWeekScreenPreview()
}

@Preview(
    name = "night_mode",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
private fun WeekScreenNightPreview() {
    BasedWeekScreenPreview()
}
