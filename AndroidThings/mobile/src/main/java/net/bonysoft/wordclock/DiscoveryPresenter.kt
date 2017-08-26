package net.bonysoft.wordclock

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import timber.log.Timber
import java.nio.charset.Charset


class DiscoveryPresenter(context: Context, val serviceId: String) {

    private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(connectionHint: Bundle?) {
                    startDiscovery()
                }

                override fun onConnectionSuspended(cause: Int) {
                    Timber.e("Connection suspended", cause)
                }
            })
            .addOnConnectionFailedListener { result -> Timber.e("Connection failed", result) }
            .addApi(Nearby.CONNECTIONS_API)
            .build()

    fun startPresenting() {
        googleApiClient.connect()
    }

    private fun startDiscovery() {
        Nearby.Connections.startDiscovery(
                googleApiClient,
                serviceId,
                object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(endpointId: String?, info: DiscoveredEndpointInfo?) {
                        Timber.d("onEndpointFound:" + endpointId, info)
                        if (endpointId != null) {
                            requestConnection(endpointId)
                        }
                    }

                    override fun onEndpointLost(endpointId: String?) {
                        Timber.d("onEndpointLost:" + endpointId)
                    }
                },
                DiscoveryOptions(Strategy.P2P_STAR)
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

    private fun requestConnection(endpointId: String) {
        Nearby.Connections.requestConnection(
                googleApiClient,
                "clientId",
                endpointId,
                object : ConnectionLifecycleCallback() {
                    override fun onConnectionResult(endpointId: String?, result: ConnectionResolution?) {
                        Timber.d("connectionResult from " + endpointId, result)
                        if (result != null && result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                            sendData(endpointId)
                        }
                    }

                    override fun onDisconnected(endpointId: String?) {
                        Timber.d("onDisconnected from " + endpointId)
                    }

                    override fun onConnectionInitiated(endpointId: String?, connectionInfo: ConnectionInfo?) {
                        Timber.d("onConnectionInitiated from " + endpointId, connectionInfo)
                        Nearby.Connections.acceptConnection(
                                googleApiClient,
                                endpointId,
                                object : PayloadCallback() {
                                    override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
                                        Timber.d("onPayloadReceived", payload)
                                        if (payload != null) {
                                            val bytes = payload.asBytes()
                                            Timber.d("asString", kotlin.text.String(ByteArray(bytes!!.size, { i -> bytes[i] })))
                                        }
                                    }

                                    override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
                                        Timber.d("onPayloadTransferUpdate")
                                    }
                                }
                        );
                    }
                }
        )
    }

    private fun sendData(endpointId: String?) {
        val bytes = "Hola!".toByteArray(Charset.forName("UTF-8"))
        Timber.d("stringValue", String(bytes, Charset.forName("UTF-8")))
        Nearby.Connections.sendPayload(googleApiClient, endpointId, Payload.fromBytes(bytes))
    }

    fun stopPresenting() {
        googleApiClient.disconnect()
    }
}


