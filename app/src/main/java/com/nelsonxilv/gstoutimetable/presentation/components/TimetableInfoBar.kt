package com.nelsonxilv.gstoutimetable.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.presentation.theme.GSTOUTimetableTheme
import com.nelsonxilv.gstoutimetable.utils.customMarquee

private const val FirstSubgroup = 1
private const val SecondSubgroup = 2
private const val DataMaxLines = 1

@Composable
fun TimetableInfoBar(
    showFilterChips: Boolean,
    date: String,
    weekNumber: Int,
    selectedSubgroupNumber: Int,
    onFilterChipClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_large))
    ) {
        Column(modifier = Modifier.weight(weight = 1f)) {
            Text(
                text = date,
                maxLines = DataMaxLines,
                modifier = Modifier.customMarquee()
            )

            Text(
                text = stringResource(R.string.number_week, weekNumber),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        val subgroups = listOf(FirstSubgroup, SecondSubgroup)

        if (showFilterChips) {
            Spacer(modifier = Modifier.width(width = dimensionResource(id = R.dimen.padding_medium)))
            FilterChips(
                subgroups = subgroups,
                selectedSubgroupNumber = selectedSubgroupNumber,
                onFilterChipClick = onFilterChipClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimetableInfoBarPreview() {
    GSTOUTimetableTheme {
        Surface {
            TimetableInfoBar(
                showFilterChips = true,
                date = "1 Сентября, Понедельник",
                weekNumber = 1,
                onFilterChipClick = {},
                selectedSubgroupNumber = 1
            )
        }
    }
}