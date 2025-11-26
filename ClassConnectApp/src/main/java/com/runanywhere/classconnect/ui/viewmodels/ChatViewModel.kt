package com.runanywhere.classconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.classconnect.data.AssignmentTask
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import com.runanywhere.sdk.models.ModelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Simple Message Data Class
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// ViewModel
class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _currentModelId = MutableStateFlow<String?>(null)
    val currentModelId: StateFlow<String?> = _currentModelId

    private val _statusMessage = MutableStateFlow<String>("Initializing...")
    val statusMessage: StateFlow<String> = _statusMessage

    private val _tasks = MutableStateFlow<List<AssignmentTask>>(emptyList())
    val tasks: StateFlow<List<AssignmentTask>> = _tasks

    init {
        loadAvailableModels()
    }

    // Task Management Functions
    fun addTask(title: String, description: String, dueAtMillis: Long) {
        val task = AssignmentTask(title = title, description = description, dueAtMillis = dueAtMillis)
        _tasks.update { it + task }
    }

    fun toggleTaskCompleted(id: Long, completed: Boolean) {
        _tasks.update { list ->
            list.map { if (it.id == id) it.copy(isCompleted = completed) else it }
        }
    }

    fun deleteTask(id: Long) {
        _tasks.update { it.filterNot { t -> t.id == id } }
    }

    // Message Management Functions
    fun sendMessage(text: String) {
        if (_currentModelId.value == null) {
            _statusMessage.value = "Please load a model first"
            return
        }

        // Add user message
        addMessage(ChatMessage(text, isUser = true))

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Generate response with streaming
                var assistantResponse = ""
                RunAnywhere.generateStream(text).collect { token ->
                    assistantResponse += token

                    // Update assistant message in real-time
                    val currentMessages = _messages.value.toMutableList()
                    if (currentMessages.lastOrNull()?.isUser == false) {
                        currentMessages[currentMessages.lastIndex] =
                            ChatMessage(assistantResponse, isUser = false)
                    } else {
                        currentMessages.add(ChatMessage(assistantResponse, isUser = false))
                    }
                    _messages.value = currentMessages
                }
            } catch (e: Exception) {
                addMessage(ChatMessage("Error: ${e.message}", isUser = false))
            }

            _isLoading.value = false
        }
    }

    fun addMessage(message: ChatMessage) {
        _messages.update { it + message }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }

    // Model Management Functions
    private fun loadAvailableModels() {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Loading available models..."
                val models = listAvailableModels()
                _availableModels.value = models
                _statusMessage.value = "Ready - Please download and load a model"
            } catch (e: Exception) {
                _statusMessage.value = "Error loading models: ${e.message}"
            }
        }
    }

    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Downloading model..."
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    _statusMessage.value = "Downloading: ${(progress * 100).toInt()}%"
                }
                _downloadProgress.value = null
                _statusMessage.value = "Download complete! Please load the model."
            } catch (e: Exception) {
                _statusMessage.value = "Download failed: ${e.message}"
                _downloadProgress.value = null
            }
        }
    }

    fun loadModel(modelId: String) {
        viewModelScope.launch {
            try {
                _statusMessage.value = "Loading model..."
                val success = RunAnywhere.loadModel(modelId)
                if (success) {
                    _currentModelId.value = modelId
                    _statusMessage.value = "Model loaded! Ready to chat."
                } else {
                    _statusMessage.value = "Failed to load model"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error loading model: ${e.message}"
            }
        }
    }

    fun unloadModel() {
        viewModelScope.launch {
            try {
                RunAnywhere.unloadModel()
                _currentModelId.value = null
                _statusMessage.value = "Model unloaded"
            } catch (e: Exception) {
                _statusMessage.value = "Error unloading model: ${e.message}"
            }
        }
    }

    fun refreshModels() {
        loadAvailableModels()
    }

    // Utility Functions
    fun getCurrentModelName(): String {
        return _currentModelId.value ?: "No model loaded"
    }

    fun hasLoadedModel(): Boolean {
        return _currentModelId.value != null
    }
}