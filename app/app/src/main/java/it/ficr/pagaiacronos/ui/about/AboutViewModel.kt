package it.ficr.pagaiacronos.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val donationUrl = settingsRepository.donationUrlFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
}
