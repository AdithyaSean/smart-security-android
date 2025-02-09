[Previous content remains the same until the try block in startDiscovery]

        try {
            nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            Log.d(TAG, "Started service discovery for $SERVICE_TYPE")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start discovery", e)
            isDiscovering.set(false)
            val error = "Failed to start discovery service: ${e.message}"
            _lastError.value = error
            _scanningState.value = ScanningState.Error(error)
        }
    }

    private fun restartDiscovery() {
        stopDiscovery()
        startDiscovery()
    }

    fun stopDiscovery() {
        Log.d(TAG, "Stopping camera discovery")
        try {
            discoveryListener?.let { listener ->
                nsdManager?.stopServiceDiscovery(listener)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop discovery", e)
            val error = "Failed to stop discovery: ${e.message}"
            _lastError.value = error
        }
        discoveryListener = null
        isDiscovering.set(false)
        _scanningState.value = ScanningState.Idle
        heartbeatJob?.cancel()
    }

    fun cleanup() {
        Log.d(TAG, "Cleaning up discovery service")
        stopDiscovery()
        heartbeatJob?.cancel()
        serviceScope.cancel() // Cancel all coroutines
        
        networkCallback?.let { callback ->
            try {
                connectivityManager?.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering network callback", e)
            }
        }
        networkCallback = null
        
        // Clear all references
        nsdManager = null
        connectivityManager = null
        discoveryListener = null
        resolveListener = null
        onCameraDiscoveredListener = null
        discoveredCameras.clear()
        
        // Reset state
        _lastError.value = null
        _scanningState.value = ScanningState.Idle
    }
}
