package net.bonysoft.wordclock

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import net.bonysoft.wordclock.common.Configuration
import timber.log.Timber
import java.nio.charset.Charset

class DiscoveryPresenter(context: Context, val serviceId: String, val configurationDisplayer: ConfigurationDisplayer) {

    private var currentEndpointId: String? = null

    private val connectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(connectionHint: Bundle?) {
            startDiscovery()
        }

        override fun onConnectionSuspended(cause: Int) {
            Timber.e("Connection suspended", cause)
        }
    }

    private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(connectionCallbacks)
            .addOnConnectionFailedListener { result -> Timber.e("Connection failed", result) }
            .addApi(Nearby.CONNECTIONS_API)
            .build()

    private fun startDiscovery() {
        Nearby.Connections.startDiscovery(
                googleApiClient,
                serviceId,
                endpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_CLUSTER)
        )
                .setResultCallback { result ->
                    Timber.d("startDiscovery:onResult:" + result)
                    if (result.status.isSuccess) {
                        Timber.d("startDiscovery:onResult: SUCCESS")
                    } else {
                        Timber.d("startDiscovery:onResult: FAILURE ")
                    }
                }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String?, info: DiscoveredEndpointInfo?) {
            Timber.d("onEndpointFound:" + endpointId, info)
            if (endpointId != null) {
                requestConnection(endpointId)
            }
        }

        override fun onEndpointLost(endpointId: String?) {
            configurationDisplayer.showNoDeviceAvailable()
            Timber.d("onEndpointLost:" + endpointId)
        }
    }

    private fun requestConnection(endpointId: String) {
        Nearby.Connections.requestConnection(
                googleApiClient,
                "clientId",
                endpointId,
                connectionLifecycleCallback
        )
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(endpointId: String?, result: ConnectionResolution?) {
            Timber.d("connectionResult from " + endpointId, result)
            if (result != null && result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                currentEndpointId = endpointId
            }

        }

        override fun onDisconnected(endpointId: String?) {
            Timber.d("onDisconnected from " + endpointId)
            currentEndpointId = null
        }

        override fun onConnectionInitiated(endpointId: String?, connectionInfo: ConnectionInfo?) {
            Timber.d("onConnectionInitiated from " + endpointId, connectionInfo)
            Nearby.Connections.acceptConnection(
                    googleApiClient,
                    endpointId,
                    payloadCallback
            )
            currentEndpointId = endpointId
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            Timber.d("onPayloadReceived", payload)
            if (payload != null) {
                val bytes = payload.asBytes()
                val payloadString = String(ByteArray(bytes!!.size, { i -> bytes[i] }))
                Timber.d("asString", payloadString)
                val configuration = Gson().fromJson(payloadString, Configuration::class.java)
                configurationDisplayer.showConfiguration(configuration)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
            Timber.d("onPayloadTransferUpdate")
        }
    }

    fun sendConfiguration(configuration: Configuration) {
        if (currentEndpointId ==  null) {
            return
        }
        val bytes = Gson().toJson(configuration).toByteArray(Charset.forName("UTF-8"))
        Nearby.Connections.sendPayload(googleApiClient, currentEndpointId, Payload.fromBytes(bytes))
    }

    fun startPresenting() {
        googleApiClient.connect()
    }

    fun stopPresenting() {
        googleApiClient.disconnect()
    }
}


