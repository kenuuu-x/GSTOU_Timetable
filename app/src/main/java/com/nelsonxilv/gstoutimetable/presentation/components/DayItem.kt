package com.nelsonxilv.gstoutimetable.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.Day
import com.nelsonxilv.gstoutimetable.domain.entity.DayOfWeek
import com.nelsonxilv.gstoutimetable.domain.entity.Lesson
import com.nelsonxilv.gstoutimetable.domain.entity.TimeInterval
import com.nelsonxilv.gstoutimetable.presentation.theme.GSTOUTimetableTheme

@Composable
fun DayItem(
    day: Day,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .animateContentSize()
            .clickable(indication = null, interactionSource = interactionSource) {
                expanded = !expanded
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.padding_large),
                vertical = dimensionResource(id = R.dimen.padding_medium)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = day.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = if (day.lessons.isNotEmpty()) {
                        stringResource(id = R.string.number_of_classes, day.lessons.size)
                    } else {
                        stringResource(id = R.string.no_classes)
                    },
                    style = if (day.lessons.isNotEmpty()) {
                        MaterialTheme.typography.bodyMedium
                    } else {
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
            if (day.lessons.isNotEmpty()) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                )
            }
        }

        if (expanded && day.lessons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            ShapedList(listElements = day.lessons) { lesson, shape ->
                LessonItem(
                    lesson = lesson,
                    shape = shape
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun DayItemPreview() {
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
        subgroupNumber = 0
    )

    val day = Day(
        name = "Понедельник",
        lessons = listOf(lesson, lesson2)
    )
    GSTOUTimetableTheme {
        DayItem(day = day)
    }
}