package it.ficr.pagaiacronos.ui.athlete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyAthleteViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val personalAthleteId: StateFlow<Long?> = settingsRepository.personalAthleteIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
