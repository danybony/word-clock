package net.bonysoft.wordclock

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import net.bonysoft.wordclock.common.Configuration

class MainActivity : Activity() {

    private lateinit var discoveryPresenter: DiscoveryPresenter

    val PERMISSIONS_REQUEST: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendUpdate = { configuration: Configuration -> discoveryPresenter.sendConfiguration(configuration) }
        discoveryPresenter = DiscoveryPresenter(
                this,
                getString(R.string.service_id),
                ConfigurationDisplayer(findViewById(R.id.content), sendUpdate)
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                discoveryPresenter.startPresenting()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH)
        if (allPermissionsGranted(permissions)) {
            discoveryPresenter.startPresenting()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    PERMISSIONS_REQUEST)
        }
    }

    private fun allPermissionsGranted(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        discoveryPresenter.stopPresenting()
    }
}
