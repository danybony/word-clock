package net.bonysoft.wordclock

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import timber.log.Timber
import java.nio.charset.Charset


class ConfigPresenter(context: Context, val appName: String, val serviceId: String) {

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

    fun startPresenting() {
        googleApiClient.connect()
    }

    private fun startAdvertising() {
        Nearby.Connections.startAdvertising(
                googleApiClient,
                appName,
                serviceId,
                object : ConnectionLifecycleCallback() {
                    override fun onConnectionResult(endpointId: String?, result: ConnectionResolution?) {
                        Timber.d("connectionResult from " + endpointId, result)
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
                                            Timber.d("asString", kotlin.text.String(bytes!!, Charset.forName("UTF-8")))
                                        }
                                    }

                                    override fun onPayloadTransferUpdate(endpointId: String?, update: PayloadTransferUpdate?) {
                                        Timber.d("onPayloadTransferUpdate")
                                    }
                                }
                        );
                    }

                },
                AdvertisingOptions(Strategy.P2P_STAR)
        )
                .setResultCallback { result ->
                    Timber.d("startAdvertising:onResult:", result)
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

    fun stopPresenting() {
        googleApiClient.disconnect()
    }
}


