package it.ficr.pagaiacronos.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.ficr.pagaiacronos.BuildConfig
import it.ficr.pagaiacronos.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(viewModel: AboutViewModel = hiltViewModel()) {
    val donationUrl by viewModel.donationUrl.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val openingBrowserMsg = stringResource(R.string.about_opening_browser)

    fun openUrl(url: String) {
        if (url.isBlank()) return
        scope.launch { snackbarHostState.showSnackbar(openingBrowserMsg) }
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_about)) }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // App name & version
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${stringResource(R.string.about_version)} ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.app_description),
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            // Support
            /*Text(
                stringResource(R.string.about_support),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Button(onClick = { openUrl(donationUrl) }) {
                Text(stringResource(R.string.about_donate))
            }

            HorizontalDivider()
            */
            // Credits
            Text(
                stringResource(R.string.about_credits),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text("${stringResource(R.string.about_developer)}: Juan Brenes")
            Text(
                "${stringResource(R.string.about_open_source)}: Room, Retrofit, Hilt, Jetpack Compose, OkHttp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Links
            TextButton(onClick = { openUrl("https://github.com/jbrenes/ficr-catalogue") }) {
                Text(stringResource(R.string.about_source_code))
            }

        }
    }
}
