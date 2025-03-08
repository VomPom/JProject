package julis.wang.kotlinlearn.jetpack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *
 * Created by @juliswang on 2025/03/07 17:00
 *
 * @Description 使用 Flow 和 ViewModel 结合
 */
class FlowViewModel(private val repository: DataRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            repository.getDataFlow()
                .onStart { _uiState.value = UiState.Loading }
                .catch { e -> _uiState.value = UiState.Error(e.message) }
                .collect { data ->
                    _uiState.value = UiState.Success(data)
                }
        }
    }
}

class DataRepository {
    suspend fun getDataFlow(): Flow<String> = flow {
        emit("test data...")

    }
}

sealed class UiState {
    data object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val message: String?) : UiState()
}
