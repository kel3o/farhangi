package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.StudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrgMessageDetailViewModel @Inject constructor(
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrgMessageDetailUiState>(OrgMessageDetailUiState.Loading)
    val uiState: StateFlow<OrgMessageDetailUiState> = _uiState.asStateFlow()

    fun load(messageId: String) {
        viewModelScope.launch {
            _uiState.value = OrgMessageDetailUiState.Loading
            when (val result = studioRepository.getOrgMessage(messageId)) {
                is Result.Success -> _uiState.value = OrgMessageDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value =
                    OrgMessageDetailUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun markRead() {
        val current = _uiState.value
        if (current !is OrgMessageDetailUiState.Success) return
        viewModelScope.launch {
            when (val result = studioRepository.markOrgMessageRead(current.message.id)) {
                is Result.Success -> _uiState.value = OrgMessageDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value = current.copy(
                    status = result.exception.message ?: "به‌روزرسانی ناموفق بود",
                )
                Result.Loading -> Unit
            }
        }
    }

    fun sendReply(title: String, body: String) {
        val current = _uiState.value
        if (current !is OrgMessageDetailUiState.Success) return
        val recipient = current.message.recipient
        viewModelScope.launch {
            when (studioRepository.sendOrgMessage(title, body, recipient)) {
                is Result.Success -> _uiState.value = current.copy(status = "پاسخ ارسال شد")
                is Result.Error -> _uiState.value = current.copy(status = "ارسال پاسخ ناموفق بود")
                Result.Loading -> Unit
            }
        }
    }
}
