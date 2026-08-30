package ir.farhangi.feature.studio.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.farhangi.core.common.result.Result
import ir.farhangi.core.data.repository.StudioRepository
import ir.farhangi.core.model.OrgInboxRecipient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrgInboxViewModel @Inject constructor(
    private val studioRepository: StudioRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OrgInboxUiState>(OrgInboxUiState.Loading)
    val uiState: StateFlow<OrgInboxUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(status: String? = null) {
        viewModelScope.launch {
            when (val result = studioRepository.getOrgMessages()) {
                is Result.Success -> _uiState.value = OrgInboxUiState.Success(result.data, status)
                is Result.Error -> _uiState.value =
                    OrgInboxUiState.Error(result.exception.message ?: "خطا")
                Result.Loading -> Unit
            }
        }
    }

    fun send(title: String, body: String, recipient: OrgInboxRecipient) {
        viewModelScope.launch {
            when (studioRepository.sendOrgMessage(title, body, recipient)) {
                is Result.Success -> refresh("پیام ارسال شد")
                is Result.Error -> refresh("ارسال ناموفق بود")
                Result.Loading -> Unit
            }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            when (studioRepository.markOrgMessageRead(id)) {
                is Result.Success -> refresh()
                is Result.Error -> refresh("به‌روزرسانی ناموفق بود")
                Result.Loading -> Unit
            }
        }
    }
}
