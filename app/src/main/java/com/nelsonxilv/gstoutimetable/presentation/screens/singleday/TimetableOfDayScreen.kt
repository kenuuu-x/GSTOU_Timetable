package com.nelsonxilv.gstoutimetable.presentation.screens.singleday

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.DateType
import com.nelsonxilv.gstoutimetable.presentation.components.TimetableInfoBar
import com.nelsonxilv.gstoutimetable.presentation.components.content.ContentContainer
import com.nelsonxilv.gstoutimetable.presentation.components.content.LoadingContent
import com.nelsonxilv.gstoutimetable.presentation.components.content.ResultContent
import com.nelsonxilv.gstoutimetable.presentation.screens.singleday.contract.LessonsUiEvent
import com.nelsonxilv.gstoutimetable.presentation.screens.singleday.contract.LessonsUiState
import com.nelsonxilv.gstoutimetable.presentation.theme.GSTOUTimetableTheme

@Composable
fun TimetableOfDayScreen(
    searchGroupName: String,
    dateType: DateType,
    contentPadding: PaddingValues,
    onCardClick: () -> Unit,
) {
    val viewModel =
        hiltViewModel<TimetableOfDayViewModel, TimetableOfDayViewModel.Factory>(
            creationCallback = { factory -> factory.create(dateType) }
        )
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    TimetableOfDayContent(
        uiState = uiState,
        searchGroupName = searchGroupName,
        onEvent = viewModel::handleEvent,
        contentPadding = contentPadding,
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
private fun TimetableOfDayContent(
    uiState: LessonsUiState = LessonsUiState(),
    searchGroupName: String = "",
    contentPadding: PaddingValues = PaddingValues(),
    onEvent: (LessonsUiEvent) -> Unit = {},
    onCardClick: () -> Unit = {},
    onCopied: () -> Unit = {},
) {

    LaunchedEffect(searchGroupName) {
        onEvent(LessonsUiEvent.OnGroupSearch(searchGroupName))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        TimetableInfoBar(
            showFilterChips = uiState.showFilterChips,
            selectedSubgroupNumber = uiState.selectedSubgroupNumber,
            date = uiState.dateInfo.currentFormattedDate,
            weekNumber = uiState.dateInfo.currentWeekNumber,
            onFilterChipClick = { groupNum ->
                onEvent(LessonsUiEvent.OnSubgroupChipClick(groupNum))
            }
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))

        AnimatedContent(
            targetState = uiState,
            label = "Animated content"
        ) { targetState ->

            when {
                targetState.isLoading -> LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 140.dp)
                )

                targetState.isInitialState -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 140.dp)
                ) {
                    ContentContainer(
                        iconRes = targetState.greetingImageId,
                        textRes = targetState.greetingMessageId,
                        onCardClick = onCardClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                targetState.isEmptyLessonList -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 140.dp)
                ) {
                    ContentContainer(
                        iconRes = targetState.emptyLessonListImageId,
                        textRes = targetState.emptyLessonListMessageId,
                        onCardClick = onCardClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                targetState.isLoadingLessonsError -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 140.dp)
                ) {
                    ContentContainer(
                        iconRes = targetState.loadingLessonsErrorImageId,
                        textRes = targetState.loadingLessonsErrorMessageId,
                        optionalSecondText = targetState.errorMessage,
                        onCardClick = onCardClick,
                        onCopied = onCopied,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                targetState.lessons.isNotEmpty() -> ResultContent(
                    lessons = targetState.lessons,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimetableOfDayContentPreview() {
    GSTOUTimetableTheme {
        TimetableOfDayContent()
    }
}