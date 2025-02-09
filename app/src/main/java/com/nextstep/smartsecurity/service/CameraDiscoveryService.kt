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

    
    private fun initializeResolveListener() {
        resolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.d(TAG, "Resolve succee")

                val host = serviceInfo.host.hostAddress ?: return
                val port = serviceInfo.port
                val streamPath = serviceInfo.attributes["path"]?.decode

                val cameraInfo = CameraInfo(t = host,
                    streamPath = streamPath
                )f (!discoveredCameras.contains(cameraInfo)) {
                    discoveredCameras.add(cameraInfo)
                    onCameraDiscoveredListener?.invoke(discoveredCameras.toList())
                }
            }
        }
    }
        stopDiscovery() // Stop any existing discovery
     onCameraL aed
edCameras.clear()

        discoveryListener = object : NsdManager.Disv
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
            }
    Log.e(TAG, "Discovery failed: Error code:$errorCode")
            }

            override fun onDiscoveryStarted(serviceType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onDisco
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(TAG, "D discovery succrviceName}")
                nsdManager?.resolveService(service, resolveListener)
            }

            override fun onSDLost(service: N
                Log.e(TAG, "Service lost: ${service.serviceName}")
                discoveredCameras.removeIf { it.name == service.serviceName }
                onCameraDiscoveredListener?.invoke(discoveredCameras.toList())
            }
        }

        nsdManager?.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }ed")
            }

            ovrrieun onServiceFund(service: NsdServiceInfo) {
                Log.d(TAG, "Sevicediscover succss{.srviceName})
                nsdManager?.resolveService(service, resolveListener

    fun stopDiscovery() {
        try {Lst
            discovereListener?.let {llstener ->")
               discoveredCameras.removeIf { i.nam = Nam 
                onCameraDiscoveredLisnenes?.invoke(discoveredCameras.toList())nager?.stopServiceDiscovery(listener)
            }
}}

        dicrsSERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryLiten)
    }

    fun stopDiscoery() {
        try {
            dsoveryListener?.let { listenr ->
               nsdManager?.stopSevicDicry(l
            }
} catch (e: Exception) {
            Log.e(TAG, "Failed to stop discovery", e)
        }
        discoveryListener = null
    }
}
