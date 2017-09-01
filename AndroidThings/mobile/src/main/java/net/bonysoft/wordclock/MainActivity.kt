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

        val sendUpdate = {configuration:Configuration -> discoveryPresenter.sendConfiguration(configuration)}
        discoveryPresenter = DiscoveryPresenter(
                this,
                getString(R.string.service_id) ,
                ConfigurationDisplayer(findViewById(R.id.content), sendUpdate)
        )

        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH)
        if (!allPermissionsGranted(permissions)) {
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

    //TODO: do things when permissions are granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onStart() {
        super.onStart()
        discoveryPresenter.startPresenting()
    }

    override fun onStop() {
        super.onStop()
        discoveryPresenter.stopPresenting()
    }
}
