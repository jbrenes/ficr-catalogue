package it.ficr.pagaiacronos.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.local.dao.ClubProjection
import it.ficr.pagaiacronos.data.repository.AthleteRepository
import it.ficr.pagaiacronos.data.repository.ResultRepository
import it.ficr.pagaiacronos.data.repository.ResultsFilter
import it.ficr.pagaiacronos.domain.model.ResultRow
import it.ficr.pagaiacronos.domain.model.toDomain
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultsUiState(
    val results: List<ResultRow> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val clubs: List<ClubProjection> = emptyList(),
    val venues: List<String> = emptyList()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val resultRepository: ResultRepository,
    private val athleteRepository: AthleteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState(isLoading = true))
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(ResultsFilter())
    val filter: StateFlow<ResultsFilter> = _filter.asStateFlow()

    private var currentPage = 0

    init {
        loadFilterOptions()
        // Reload on filter change (debounced for text input)
        viewModelScope.launch {
            _filter
                .debounce(300)
                .distinctUntilChanged()
                .collect { applyFilter() }
        }
    }

    fun updateFilter(new: ResultsFilter) { _filter.value = new }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                currentPage++
                val page = resultRepository.getPage(_filter.value, currentPage)
                val rows = page.map { it.toDomain() }
                _uiState.update { s ->
                    s.copy(
                        results = s.results + rows,
                        isLoadingMore = false,
                        hasMore = rows.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false, error = e.message) }
            }
        }
    }

    private fun applyFilter() {
        viewModelScope.launch {
            currentPage = 0
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val rows = resultRepository.getPage(_filter.value, 0).map { it.toDomain() }
                _uiState.update { s ->
                    s.copy(results = rows, isLoading = false, hasMore = rows.isNotEmpty())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            val clubs = athleteRepository.getDistinctClubs()
            val venues = athleteRepository.getDistinctVenues()
            _uiState.update { it.copy(clubs = clubs, venues = venues) }
        }
    }
}
