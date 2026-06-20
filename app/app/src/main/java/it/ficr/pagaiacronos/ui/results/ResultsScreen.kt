package it.ficr.pagaiacronos.ui.results

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.ficr.pagaiacronos.R

import it.ficr.pagaiacronos.ui.components.EmptyState
import it.ficr.pagaiacronos.ui.components.FilterBar
import it.ficr.pagaiacronos.ui.components.ResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    onAthleteClick: (Long) -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()


    // Trigger pagination when near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0)
            totalItems > 0 && lastVisible >= totalItems - 5
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_results)) },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 8.dp,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            item {
                FilterBar(
                    filter = filter,
                    clubs = state.clubs,
                    venues = state.venues,
                    state.distances,
                    athleteSuggestions = emptyList(),
                    onFilterChange = viewModel::updateFilter,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.results.isEmpty()) {
                item {
                    EmptyState(
                        title = stringResource(R.string.results_empty_title),
                        subtitle = stringResource(R.string.results_empty_subtitle)
                    )
                }
            } else {

                    items(state.results, key = { "${it.resultId}_${it.primaryAthleteId}" }) { row ->
                        ResultCard(
                            result = row,
                            onClick = { row.primaryAthleteId?.let { onAthleteClick(it) } },

                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(8.dp))
                    }
                }
            }
        }
    }
}
