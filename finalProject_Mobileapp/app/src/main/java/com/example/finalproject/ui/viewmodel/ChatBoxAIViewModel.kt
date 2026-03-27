package com.example.finalproject.ui.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.remote.api.AiTutorApiService
import com.example.finalproject.data.remote.model.AiChatMessageItem
import com.example.finalproject.data.remote.model.AiChatRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBoxAIViewModel @Inject constructor(
    private val apiService: AiTutorApiService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiChatMessageItem>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Dùng TextFieldValue để kiểm soát vị trí con trỏ (cursor) khi chèn ký hiệu toán
    private val _inputValue = MutableStateFlow(TextFieldValue(""))
    val inputValue = _inputValue.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val response = apiService.getHistory()
                _messages.value = response.data ?: emptyList()
            } catch (e: Exception) {
                // Lịch sử trống khi chưa có hội thoại nào - bình thường
            }
        }
    }

    fun onInputValueChange(value: TextFieldValue) {
        _inputValue.value = value
    }

    /**
     * Chèn ký hiệu LaTeX vào đúng vị trí con trỏ.
     * Sau khi chèn, con trỏ sẽ được đặt vào vị trí {} đầu tiên (nếu có) để học sinh gõ tiếp.
     */
    fun insertSymbol(latex: String) {
        val current = _inputValue.value
        val cursorPos = current.selection.end
        val newText = current.text.substring(0, cursorPos) + latex + current.text.substring(cursorPos)

        // Tìm vị trí {} đầu tiên để đặt cursor vào bên trong
        val firstBrace = newText.indexOf("{}", cursorPos)
        val newCursorPos = if (firstBrace != -1) firstBrace + 1 else cursorPos + latex.length

        _inputValue.value = TextFieldValue(
            text = newText,
            selection = TextRange(newCursorPos)
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isLoading.value) return

        // Reset input field
        _inputValue.value = TextFieldValue("")

        // Thêm tin nhắn user vào danh sách ngay lập tức (optimistic update)
        val userMsg = AiChatMessageItem(
            id = System.currentTimeMillis(),
            role = "USER",
            content = text,
            createdAt = null
        )
        _messages.value = _messages.value + userMsg
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = apiService.sendMessage(AiChatRequest(message = text))
                if (response.data != null) {
                    val aiMsg = AiChatMessageItem(
                        id = System.currentTimeMillis() + 1,
                        role = "ASSISTANT",
                        content = response.data.reply,
                        createdAt = null
                    )
                    _messages.value = _messages.value + aiMsg
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Không thể kết nối. Kiểm tra lại mạng nhé!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                apiService.clearHistory()
                _messages.value = emptyList()
            } catch (e: Exception) {
                _error.value = "Không thể xoá lịch sử"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
