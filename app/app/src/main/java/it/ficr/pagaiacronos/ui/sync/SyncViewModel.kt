package it.ficr.pagaiacronos.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.repository.SettingsRepository
import it.ficr.pagaiacronos.data.repository.SyncRepository
import it.ficr.pagaiacronos.domain.model.SyncLog
import it.ficr.pagaiacronos.domain.model.toDomain
import it.ficr.pagaiacronos.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncLog: SyncLog? = null,
    val message: String? = null,
    val isError: Boolean = false
)

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val settingsRepository: SettingsRepository,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<SyncLog>> = syncRepository.getRecentLogsFlow()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val syncUrl: StateFlow<String> = settingsRepository.syncUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    init {
        viewModelScope.launch {
            val latest = syncRepository.getLatestLog()?.toDomain()
            _uiState.update { it.copy(lastSyncLog = latest) }
        }
    }

    fun syncNow() {
        if (_uiState.value.isSyncing) return
        viewModelScope.launch {
            if (!networkUtils.isConnected()) {
                _uiState.update { it.copy(message = "Connessione non disponibile", isError = true) }
                return@launch
            }
            val url = settingsRepository.getSyncUrl()
            _uiState.update { it.copy(isSyncing = true, message = null, isError = false) }
            try {
                val result = syncRepository.performSync(url)
                val latest = syncRepository.getLatestLog()?.toDomain()
                _uiState.update {
                    it.copy(
                        isSyncing = false,
                        lastSyncLog = latest,
                        message = "Completato: ${result.recordsUpdated} record aggiornati",
                        isError = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSyncing = false, message = e.message ?: "Errore", isError = true)
                }
            }
        }
    }

    fun clearMessage() { _uiState.update { it.copy(message = null) } }
}
