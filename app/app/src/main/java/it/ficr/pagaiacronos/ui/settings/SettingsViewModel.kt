package it.ficr.pagaiacronos.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.repository.SettingsRepository
import it.ficr.pagaiacronos.data.repository.SyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val syncUrl: String = "",
    val autoSync: Boolean = false,
    val showClearDialog: Boolean = false,
    val clearSuccessMessage: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.syncUrlFlow.collect { url ->
                _uiState.update { it.copy(syncUrl = url) }
            }
        }
        viewModelScope.launch {
            settingsRepository.autoSyncFlow.collect { enabled ->
                _uiState.update { it.copy(autoSync = enabled) }
            }
        }
    }

    fun onSyncUrlChange(url: String) { _uiState.update { it.copy(syncUrl = url) } }

    fun saveSyncUrl() {
        viewModelScope.launch { settingsRepository.setSyncUrl(_uiState.value.syncUrl) }
    }

    fun toggleAutoSync(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoSync(enabled) }
    }

    fun requestClear() { _uiState.update { it.copy(showClearDialog = true) } }
    fun dismissClear() { _uiState.update { it.copy(showClearDialog = false) } }

    fun confirmClear() {
        viewModelScope.launch {
            syncRepository.clearAll()
            _uiState.update { it.copy(showClearDialog = false, clearSuccessMessage = true) }
        }
    }

    fun clearSuccessShown() { _uiState.update { it.copy(clearSuccessMessage = false) } }
}
