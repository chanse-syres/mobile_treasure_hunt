package com.chanse.cs492.treasurehunt.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chanse.cs492.treasurehunt.data.HuntRepository
import com.chanse.cs492.treasurehunt.data.model.Clue
import com.chanse.cs492.treasurehunt.data.model.Hunt
import com.chanse.cs492.treasurehunt.util.LocationUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TreasureUiState(
    val hunts: List<Hunt> = emptyList(),
    val selectedHunt: Hunt? = null,
    val clueIndex: Int = 0,
    val elapsedSeconds: Long = 0L,
    val timerRunning: Boolean = false,
    val hintVisible: Boolean = false,
    val wrongLocationVisible: Boolean = false,
    val huntCompleted: Boolean = false,
    val lastDistanceMeters: Double? = null
) {
    val currentClue: Clue?
        get() = selectedHunt?.clues?.getOrNull(clueIndex)
}

class TreasureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuntRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(TreasureUiState())
    val uiState: StateFlow<TreasureUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadHunts()
    }

    fun loadHunts() {
        _uiState.update { it.copy(hunts = repository.loadHunts()) }
    }

    fun selectHunt(huntId: String): Boolean {
        val hunt = repository.getHuntById(huntId) ?: return false

        _uiState.update {
            it.copy(
                selectedHunt = hunt,
                clueIndex = 0,
                elapsedSeconds = 0L,
                timerRunning = false,
                hintVisible = false,
                wrongLocationVisible = false,
                huntCompleted = false,
                lastDistanceMeters = null
            )
        }

        return hunt.enabled && hunt.clues.isNotEmpty()
    }

    fun startTimer() {
        if (timerJob?.isActive == true) return

        _uiState.update { it.copy(timerRunning = true) }

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { state ->
                    state.copy(elapsedSeconds = state.elapsedSeconds + 1)
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(timerRunning = false) }
    }

    fun resetHunt() {
        pauseTimer()
        _uiState.update { state ->
            state.copy(
                selectedHunt = null,
                clueIndex = 0,
                elapsedSeconds = 0L,
                hintVisible = false,
                wrongLocationVisible = false,
                huntCompleted = false,
                lastDistanceMeters = null
            )
        }
    }

    fun showHint(show: Boolean) {
        _uiState.update { it.copy(hintVisible = show) }
    }

    fun dismissWrongLocation() {
        _uiState.update { it.copy(wrongLocationVisible = false) }
    }

    fun verifyLocation(userLat: Double, userLon: Double): Boolean {
        val clue = _uiState.value.currentClue ?: return false

        val distance = LocationUtils.haversineMeters(
            lat1 = userLat,
            lon1 = userLon,
            lat2 = clue.latitude,
            lon2 = clue.longitude
        )

        val matched = distance <= clue.radiusMeters

        _uiState.update {
            it.copy(
                lastDistanceMeters = distance,
                wrongLocationVisible = !matched
            )
        }

        return matched
    }

    fun advanceClueOrComplete() {
        val state = _uiState.value
        val hunt = state.selectedHunt ?: return

        val nextIndex = state.clueIndex + 1

        if (nextIndex > hunt.clues.lastIndex) {
            pauseTimer()
            _uiState.update { it.copy(huntCompleted = true, hintVisible = false) }
        } else {
            _uiState.update {
                it.copy(
                    clueIndex = nextIndex,
                    hintVisible = false,
                    wrongLocationVisible = false
                )
            }
        }
    }
}