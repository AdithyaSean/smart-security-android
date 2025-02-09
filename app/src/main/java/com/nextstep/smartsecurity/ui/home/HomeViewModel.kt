package com.nextstep.smartsecurity.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.smartsecurity.service.CameraDiscoveryService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val discoveryService = CameraDiscoveryService(application)
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    
    val cameras = MutableStateFlow<List<CameraDiscoveryService.CameraInfo>>(emptyList())
    val scanningState = discoveryService.scanningState
    val lastError = discoveryService.lastError
    
    init {
        discoveryService.setOnCameraDiscoveredListener { cameraList ->
            viewModelScope.launch {
                cameras.emit(cameraList)
                if (_isRefreshing.value) {
                    _isRefreshing.value = false
                }
            }
        }
        startDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        discoveryService.cleanup()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            discoveryService.startDiscovery()
            
            // Auto-disable refreshing after timeout
            delay(10000)
            if (_isRefreshing.value) {
                _isRefreshing.value = false
            }
        }
    }

    fun startDiscovery() {
        discoveryService.startDiscovery()
    }

    fun stopDiscovery() {
        discoveryService.stopDiscovery()
    }

    fun getStreamUrl(camera: CameraDiscoveryService.CameraInfo): String {
        return "http://${camera.host}:${camera.port}${camera.streamPath}"
    }
}
