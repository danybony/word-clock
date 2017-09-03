package net.bonysoft.wordclock

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import net.bonysoft.wordclock.common.Configuration
import timber.log.Timber
import java.nio.charset.Charset

class ConfigPresenter(context: Context, val appName: String, val serviceId: String, onNewConfigurationReceived: (Configuration) -> Unit) {

    private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(connectionHint: Bundle?) {
                    startAdvertising()
                }

                override fun onConnectionSuspended(cause: Int) {
                    Timber.e("Connection suspended", cause)
                }
            })
            .addOnConnectionFailedListener { result -> Timber.e("Connection failed", result) }
            .addApi(Nearby.CONNECTIONS_API)
            .build()

    private fun startAdvertising() {
        Nearby.Connections.startAdvertising(
                googleApiClient,
                appName,
                serviceId,
                connectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR)
        )
                .setResultCallback { result ->
                    Timber.d("startAdvertising:onResult:", result.status)
                    if (result.status.isSuccess) {
                        Timber.d("startAdvertising:onResult: SUCCESS")
                    } else {
                        Timber.d("startAdvertising:onResult: FAILURE ")
                        val statusCode = result.status.statusCode
                        if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                            Timber.d("STATUS_ALREADY_ADVERTISING")
                        } else {
                            Timber.d("STATE_READY")
                        }
                    }
                }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(endpointId: String?, result: ConnectionResolution?) {
            Timber.d("connectionResult from " + endpointId, result)
            sendCurrentConfiguration(Configuration(Color.RED), endpointId) // TODO
        }

        override fun onDisconnected(endpointId: String?) {
            Timber.d("onDisconnected from " + endpointId)
        }

        override fun onConnectionInitiated(endpointId: String?, connectionInfo: ConnectionInfo?) {
            Timber.d("onConnectionInitiated from " + endpointId, connectionInfo)
            Nearby.Connections.acceptConnection(
                    googleApiClient,
                    endpointId,
                    payloadCallback
            )
            sendCurrentConfiguration(Configuration(Color.RED), endpointId) // TODO
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
                onNewConfigurationReceived(configuration)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
            Timber.d("onPayloadTransferUpdate")
        }
    }

    private fun sendCurrentConfiguration(configuration: Configuration, endpointId: String?) {
        val bytes = Gson().toJson(configuration).toByteArray(Charset.forName("UTF-8"))
        Nearby.Connections.sendPayload(googleApiClient, endpointId, Payload.fromBytes(bytes))
    }

    fun startPresenting() {
        googleApiClient.connect()
    }

    fun stopPresenting() {
        googleApiClient.disconnect()
    }
}


