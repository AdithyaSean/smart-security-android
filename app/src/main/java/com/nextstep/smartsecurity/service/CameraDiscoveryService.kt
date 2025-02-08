package com.nextstep.smartsecurity.service

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class CameraDiscoveryService(private val context: Context) {
    private var nsdManager: NsdManager? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var resolveListener: NsdManager.ResolveListener? = null
    
    private val discoveredCameras = mutableListOf<CameraInfo>()
    private var onCameraDiscoveredListener: ((List<CameraInfo>) -> Unit)? = null

    companion object {
        private const val TAG = "CameraDiscoveryService"
        private const val SERVICE_TYPE = "_smartcam._tcp."
    }

    data class CameraInfo(
        val name: String,
        val host: String,
        val port: Int,
        val streamPath: String
    )

    init {
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        initializeResolveListener()
    }

    private fun initializeResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(TAG, "Resolve succeeded: ${serviceInfo.serviceName}")

                val host = serviceInfo.host.hostAddress ?: return
                val port = serviceInfo.port
                val streamPath = serviceInfo.attributes["path"]?.decodeToString() ?: "/stream"
                val name = serviceInfo.serviceName

                val cameraInfo = CameraInfo(
                    name = name,
                    host = host,
                    port = port,
                    streamPath = streamPath
                )

                if (!discoveredCameras.contains(cameraInfo)) {
                    discoveredCameras.add(cameraInfo)
                    onCameraDiscoveredListener?.invoke(discoveredCameras.toList())
                }
            }
        }
    }

    fun startDiscovery(onCameraDiscovered: (List<CameraInfo>) -> Unit) {
        stopDiscovery() // Stop any existing discovery
        onCameraDiscoveredListener = onCameraDiscovered
        discoveredCameras.clear()

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
            }

            override fun onDiscoveryStarted(serviceType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(TAG, "Service discovery stopped")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(TAG, "Service discovery success: ${service.serviceName}")
                nsdManager?.resolveService(service, resolveListener)
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(TAG, "Service lost: ${service.serviceName}")
                discoveredCameras.removeIf { it.name == service.serviceName }
                onCameraDiscoveredListener?.invoke(discoveredCameras.toList())
            }
        }

        nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        try {
            discoveryListener?.let { listener ->
                nsdManager?.stopServiceDiscovery(listener)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop discovery", e)
        }
        discoveryListener = null
    }
}
