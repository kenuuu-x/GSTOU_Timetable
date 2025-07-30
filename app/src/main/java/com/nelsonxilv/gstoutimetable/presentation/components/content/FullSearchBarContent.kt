package com.nelsonxilv.gstoutimetable.presentation.components.content

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.nelsonxilv.gstoutimetable.R
import com.nelsonxilv.gstoutimetable.domain.entity.Group
import com.nelsonxilv.gstoutimetable.presentation.components.GroupItem
import com.nelsonxilv.gstoutimetable.presentation.theme.DefaultShape

private const val SharedContentStateKey = "shared_content_state"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FullSearchBarContent(
    savedGroupList: List<Group>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSearch: (String) -> Unit,
    onActiveChanged: (Boolean) -> Unit,
    onGroupItemClick: (String) -> Unit,
    onClearIconButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = SharedContentStateKey),
                animatedVisibilityScope = animatedVisibilityScope
            )
        ) {
            FullSearchBar(
                savedGroups = savedGroupList,
                onSearch = { onSearch(it) },
                onActiveChanged = { onActiveChanged(it) },
                onGroupItemClick = { onGroupItemClick(it) },
                onClearIconButtonClick = { onClearIconButtonClick(it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullSearchBar(
    savedGroups: List<Group>,
    onSearch: (String) -> Unit,
    onActiveChanged: (Boolean) -> Unit,
    onGroupItemClick: (String) -> Unit,
    onClearIconButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by rememberSaveable { mutableStateOf("") }

    val onActiveChange: (Boolean) -> Unit = { active -> onActiveChanged(active) }
    val colors = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        dividerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    )

    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
                onQueryChange = { query -> searchText = query },
                onSearch = { query ->
                    onSearch(query)
                    onActiveChanged(false)
                },
                expanded = true,
                onExpandedChange = onActiveChange,
                placeholder = { Text(text = stringResource(R.string.search_by_group)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )
        },
        expanded = true,
        onExpandedChange = onActiveChange,
        colors = colors
    ) {
        if (savedGroups.isNotEmpty()) {

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))

            LazyColumn(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_large)),
                verticalArrangement = Arrangement.spacedBy(
                    space = dimensionResource(id = R.dimen.padding_medium)
                )
            ) {
                items(savedGroups) {
                    GroupItem(
                        group = it,
                        onGroupItemClick = onGroupItemClick,
                        onClearIconButtonCLick = onClearIconButtonClick,
                        shape = DefaultShape
                    )
                }
            }
        }
    }
}