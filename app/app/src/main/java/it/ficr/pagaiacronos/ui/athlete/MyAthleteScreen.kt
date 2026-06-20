package it.ficr.pagaiacronos.ui.athlete

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.ficr.pagaiacronos.R
import it.ficr.pagaiacronos.ui.components.EmptyState

/**
 * Entry point for the "Athlete" tab. Resolves the personal athlete id from
 * settings and hands off navigation to the shared athlete profile route.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAthleteScreen(
    onResolved: (Long) -> Unit,
    viewModel: MyAthleteViewModel = hiltViewModel()
) {
    val athleteId by viewModel.personalAthleteId.collectAsState()
    val id = athleteId

    LaunchedEffect(id) {
        if (id != null) onResolved(id)
    }

    if (id == null) {
        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_athlete)) }) }
        ) { padding ->
            EmptyState(
                title = stringResource(R.string.profile_no_personal_athlete),
                subtitle = stringResource(R.string.profile_no_personal_athlete_action),
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        }
    }
}
