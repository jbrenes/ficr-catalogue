package it.ficr.pagaiacronos.ui.athlete

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.ficr.pagaiacronos.R
import it.ficr.pagaiacronos.ui.components.EmptyState
import it.ficr.pagaiacronos.ui.components.PersonalBestTable
import it.ficr.pagaiacronos.ui.components.ResultCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteProfileScreen(
    athleteId: Long,
    onBack: () -> Unit,
    viewModel: AthleteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val total = info.totalItemsCount
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && last >= total - 5
        }
    }
    LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.loadNextPage() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val a = state.athlete
                    Text(if (a != null) "${a.lastName} ${a.firstName}" else "")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                Modifier.fillMaxWidth().padding(padding).padding(32.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 8.dp,
                start = 8.dp,
                end = 8.dp
            )
        ) {
            // Clubs & nationality header
            state.athlete?.let { a ->
                item {
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        val clubs = state.clubs
                        if (clubs.isNotEmpty()) {
                            clubs.forEach { club ->
                                Text(
                                    text = club,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else if (!a.club.isNullOrBlank()) {
                            Text(
                                text = a.club,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = a.nationality,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Personal bests
            if (state.personalBests.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(R.string.profile_personal_bests))
                    PersonalBestTable(
                        bests = state.personalBests,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // All results
            item { SectionHeader(stringResource(R.string.profile_all_results)) }

            if (state.results.isEmpty()) {
                item {
                    EmptyState(title = stringResource(R.string.profile_no_results))
                }
            } else {
                items(state.results, key = { it.resultId }) { row ->
                    ResultCard(
                        result = row,
                        onClick = {},
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                if (state.isLoadingMore) {
                    item { LinearProgressIndicator(Modifier.fillMaxWidth().padding(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    HorizontalDivider()
}
