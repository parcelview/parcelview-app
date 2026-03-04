package dev.parcelview.feature.parcels.impl

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParcelsViewModel : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val parcels: List<String>) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class UiEvent {
        data class ParcelClicked(val trackingId: String) : UiEvent()
        object RefreshRequested : UiEvent()
    }

    sealed class UiAction {
        object NoAction : UiAction()
        data class NavigateToDetail(val trackingId: String) : UiAction()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiAction = MutableStateFlow<UiAction>(UiAction.NoAction)
    val uiAction: StateFlow<UiAction> = _uiAction.asStateFlow()

    fun onEvent(event: UiEvent) { /* TODO */ }

    fun onActionConsumed() { _uiAction.value = UiAction.NoAction }
}
