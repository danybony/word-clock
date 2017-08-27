package net.bonysoft.wordclock

import android.app.Activity
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class WordClockActivity : Activity() {

    private val matrixDisplayer = MatrixGenerator()
    private val matrixSerialiser = MatrixSerialiser()
    private val handler: Handler = Handler()
    private lateinit var configPresenter: ConfigPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configPresenter = ConfigPresenter(this,
                getString(R.string.app_name),
                getString(R.string.service_id)
        )
    }

    override fun onStart() {
        super.onStart()
        configPresenter.startPresenting()
        doUSBThings()
    }

    private fun doUSBThings() {
        val usbManager = getSystemService<UsbManager>(UsbManager::class.java)
        val connectedDevices = usbManager.deviceList
        for (device in connectedDevices.values) {
            if (device.getVendorId() === 0x1a86 && device.getProductId() === 0x7523) {
                Timber.d("Device found: " + device.deviceName)
                startSerialConnection(usbManager, device)
                break
            }
        }
    }

    private var serialConnection: UsbSerialDevice? = null

    private fun startSerialConnection(usbManager: UsbManager, device: UsbDevice) {
        val connection = usbManager.openDevice(device)
        serialConnection = UsbSerialDevice.createUsbSerialDevice(device, connection)

        if (serialConnection != null && serialConnection!!.open()) {
            serialConnection!!.setBaudRate(115200)
            serialConnection!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
            serialConnection!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
            serialConnection!!.setParity(UsbSerialInterface.PARITY_NONE)
            serialConnection!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
            serialConnection!!.read { data ->
                val dataStr = String(data, Charset.forName("UTF-8"))
                Timber.d("Received $dataStr")
            }
            scheduleUpdate()
        }
    }

    private val triggerRunnable: Runnable = Runnable {
        val now = Date()
        val matrix = matrixDisplayer.createMatrix(now)
        val matrixStr = matrixSerialiser.serialise(matrix)
        serialConnection?.write(matrixStr.toByteArray(Charset.forName("UTF-8")))

        scheduleUpdate()
    }

    private fun scheduleUpdate() {
        handler.postDelayed(triggerRunnable, 10000)
    }


    override fun onStop() {
        configPresenter.stopPresenting()
        serialConnection?.close()
        handler.removeCallbacks(triggerRunnable)
        super.onStop()
    }
}
