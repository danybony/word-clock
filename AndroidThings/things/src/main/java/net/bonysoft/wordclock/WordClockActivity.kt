package net.bonysoft.wordclock

import android.app.Activity
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler

class WordClockActivity : Activity() {

    private lateinit var configPresenter: ConfigPresenter
    private lateinit var arduinoConnectionPresenter: ArduinoConnectionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configPresenter = ConfigPresenter(this,
                getString(R.string.app_name),
                getString(R.string.service_id)
        )
        arduinoConnectionPresenter = ArduinoConnectionPresenter(
                getSystemService<UsbManager>(UsbManager::class.java),
                MatrixGenerator(),
                MatrixSerialiser(),
                Handler()
        )
    }

    override fun onStart() {
        super.onStart()
        configPresenter.startPresenting()
        arduinoConnectionPresenter.startPresenting()
    }

    override fun onStop() {
        configPresenter.stopPresenting()
        arduinoConnectionPresenter.stopPresenting()
        super.onStop()
    }
}
