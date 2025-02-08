package com.nextstep.smartsecurity.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextstep.smartsecurity.service.CameraDiscoveryService
import com.nextstep.smartsecurity.service.CameraDiscoveryService.CameraInfo

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val cameraDiscoveryService = CameraDiscoveryService(application)
    
    private val _cameras = MutableLiveData<List<CameraInfo>>()
    val cameras: LiveData<List<CameraInfo>> = _cameras
    
    private val _isScanning = MutableLiveData<Boolean>()
    val isScanning: LiveData<Boolean> = _isScanning

    init {
        startDiscovery()
    }

    fun startDiscovery() {
        _isScanning.value = true
        cameraDiscoveryService.startDiscovery { cameras ->
            _cameras.postValue(cameras)
            _isScanning.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraDiscoveryService.stopDiscovery()
    }
}
