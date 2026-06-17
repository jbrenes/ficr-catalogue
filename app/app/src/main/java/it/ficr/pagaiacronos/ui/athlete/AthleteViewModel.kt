package it.ficr.pagaiacronos.ui.athlete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.ficr.pagaiacronos.data.local.entity.AthleteEntity
import it.ficr.pagaiacronos.data.repository.AthleteRepository
import it.ficr.pagaiacronos.data.repository.ResultRepository
import it.ficr.pagaiacronos.domain.model.ChartPoint
import it.ficr.pagaiacronos.domain.model.ChartSeries
import it.ficr.pagaiacronos.domain.model.PersonalBest
import it.ficr.pagaiacronos.domain.model.ResultRow
import it.ficr.pagaiacronos.domain.model.toDomain
import it.ficr.pagaiacronos.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AthleteUiState(
    val athlete: AthleteEntity? = null,
    val personalBests: List<PersonalBest> = emptyList(),
    val chartSeries: List<ChartSeries> = emptyList(),
    val results: List<ResultRow> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AthleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val athleteRepository: AthleteRepository,
    private val resultRepository: ResultRepository
) : ViewModel() {

    private val athleteId: Long = checkNotNull(savedStateHandle["athleteId"])

    private val _uiState = MutableStateFlow(AthleteUiState())
    val uiState: StateFlow<AthleteUiState> = _uiState.asStateFlow()

    private var currentPage = 0

    init { load() }

    fun loadNextPage() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                currentPage++
                val rows = resultRepository.getResultsForAthlete(athleteId, currentPage)
                    .map { it.toDomain() }
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

    private fun load() {
        viewModelScope.launch {
            try {
                val athlete = athleteRepository.getById(athleteId)
                val pbs = resultRepository.getPersonalBests(athleteId).map { it.toDomain() }
                val timeSeries = resultRepository.getTimeSeries(athleteId)
                val results = resultRepository.getResultsForAthlete(athleteId, 0).map { it.toDomain() }

                // Group time-series by boat_class+distance for chart
                val seriesMap = timeSeries.groupBy { "${it.boatClass}_${it.distanceM}" }
                val chartSeries = seriesMap.map { (key, pts) ->
                    val first = pts.first()
                    ChartSeries(
                        label = "${first.boatClass ?: "?"} ${first.distanceM ?: "?"}m",
                        boatClass = first.boatClass,
                        distanceM = first.distanceM,
                        points = pts.mapNotNull { pt ->
                            val day = TimeUtils.isoDateToEpochDay(pt.date) ?: return@mapNotNull null
                            ChartPoint(epochDay = day, timeMs = pt.timeMs)
                        }
                    )
                }

                _uiState.update {
                    it.copy(
                        athlete = athlete,
                        personalBests = pbs,
                        chartSeries = chartSeries,
                        results = results,
                        isLoading = false,
                        hasMore = results.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
