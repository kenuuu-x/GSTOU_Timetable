package com.nelsonxilv.gstoutimetable.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.entity.ActivityType
import com.nelsonxilv.gstoutimetable.domain.entity.Lesson
import com.nelsonxilv.gstoutimetable.presentation.theme.DefaultShape
import com.nelsonxilv.gstoutimetable.utils.customMarquee

private const val MaxLinesTitle = 2
private const val MaxLinesText = 1

@Composable
fun LessonItem(
    lesson: Lesson,
    modifier: Modifier = Modifier,
    shape: Shape = DefaultShape
) {
    Card(
        shape = shape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${lesson.period}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_large)))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lesson.name,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = MaxLinesTitle,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(when(lesson.activityType) {
                                ActivityType.LECTURE -> stringResource(id = R.string.lecture)
                                ActivityType.LAB -> stringResource(id = R.string.lab)
                                ActivityType.PRACTICE -> stringResource(id = R.string.practice)
                                else -> stringResource(id = R.string.unknown)
                            })
                        }
                        append(" ${lesson.auditorium}, ${lesson.teacher}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = MaxLinesText,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.customMarquee()
                )
            }

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_large)))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = lesson.timeInterval.start,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = lesson.timeInterval.end,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}