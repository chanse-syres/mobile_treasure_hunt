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

/** Stores screen state for the treasure hunt flow. */
data class TreasureUiState(
    val hunts: List<Hunt> = emptyList(),
    val selectedHunt: Hunt? = null,
    val clueIndex: Int = 0,
    val elapsedSeconds: Long = 0L,
    val timerRunning: Boolean = false,
    val hintVisible: Boolean = false,
    val wrongLocationVisible: Boolean = false,
    val huntCompleted: Boolean = false,
    val lastDistanceMeters: Double? = null,
    val howToPlayVisible: Boolean = false
) {
    /** Returns the currently active clue. */
    val currentClue: Clue?
        get() = selectedHunt?.clues?.getOrNull(clueIndex)
}

/** Manages hunt selection, clue progress, and timer state. */
class TreasureViewModel(application: Application) : AndroidViewModel(application) {

    // Loads hunt data from raw resources.
    private val repository = HuntRepository(application.applicationContext)

    // Holds mutable UI state internally.
    private val _uiState = MutableStateFlow(TreasureUiState())

    // Exposes read-only UI state to the UI.
    val uiState: StateFlow<TreasureUiState> = _uiState.asStateFlow()

    // Tracks the running timer coroutine.
    private var timerJob: Job? = null

    // Loads hunts when the view model is created.
    init {
        loadHunts()
    }

    /** Loads all hunts into state. */
    fun loadHunts() {
        _uiState.update { it.copy(hunts = repository.loadHunts()) }
    }

    /** Selects a hunt and resets progress. */
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
                lastDistanceMeters = null,
                howToPlayVisible = true
            )
        }

        return hunt.enabled && hunt.clues.isNotEmpty()
    }

    /** Hides the tutorial and starts the timer once. */
    fun acknowledgeHowToPlay() {
        _uiState.update { it.copy(howToPlayVisible = false) }

        if (!_uiState.value.timerRunning && _uiState.value.elapsedSeconds == 0L) {
            startTimer()
        }
    }

    /** Starts the elapsed time counter. */
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

    /** Stops the elapsed time counter. */
    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(timerRunning = false) }
    }

    /** Clears the current hunt state. */
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
                lastDistanceMeters = null,
                howToPlayVisible = false
            )
        }
    }

    /** Shows or hides the hint dialog. */
    fun showHint(show: Boolean) {
        _uiState.update { it.copy(hintVisible = show) }
    }

    /** Hides the wrong-location dialog. */
    fun dismissWrongLocation() {
        _uiState.update { it.copy(wrongLocationVisible = false) }
    }

    /** Checks whether the user is within the clue radius. */
    fun verifyLocation(userLat: Double, userLon: Double): Boolean {
        val clue = _uiState.value.currentClue ?: return false

        // Computes distance from the user to the clue.
        val distance = LocationUtils.haversineMeters(
            lat1 = userLat,
            lon1 = userLon,
            lat2 = clue.latitude,
            lon2 = clue.longitude
        )

        // Marks a match when distance is inside the allowed radius.
        val matched = distance <= clue.radiusMeters

        // Stores the latest distance and dialog state.
        _uiState.update {
            it.copy(
                lastDistanceMeters = distance,
                wrongLocationVisible = !matched
            )
        }

        return matched
    }

    /** Returns true when the current clue is the last one. */
    fun isOnFinalClue(): Boolean {
        val state = _uiState.value
        val hunt = state.selectedHunt ?: return false
        return state.clueIndex == hunt.clues.lastIndex
    }

    /** Advances to the next clue if one exists. */
    fun goToNextClue() {
        val state = _uiState.value
        val hunt = state.selectedHunt ?: return
        val nextIndex = state.clueIndex + 1

        if (nextIndex <= hunt.clues.lastIndex) {
            _uiState.update {
                it.copy(
                    clueIndex = nextIndex,
                    hintVisible = false,
                    wrongLocationVisible = false,
                    howToPlayVisible = false
                )
            }
        }
    }

    /** Marks the hunt as completed. */
    fun completeHunt() {
        pauseTimer()
        _uiState.update {
            it.copy(
                huntCompleted = true,
                hintVisible = false,
                wrongLocationVisible = false,
                howToPlayVisible = false
            )
        }
    }
}