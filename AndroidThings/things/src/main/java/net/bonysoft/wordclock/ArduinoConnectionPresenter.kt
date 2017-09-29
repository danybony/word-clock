package net.bonysoft.wordclock

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import net.bonysoft.wordclock.common.Configuration
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*


class ArduinoConnectionPresenter(val activity: Activity,
                                 val usbManager: UsbManager,
                                 val matrixDisplayer: MatrixGenerator,
                                 val matrixSerialiser: MatrixSerialiser,
                                 val configurationPersister: ConfigurationPersister) {

    private var serialConnection: UsbSerialDevice? = null

    fun startPresenting() {
        val connectedDevices = usbManager.deviceList
        for (device in connectedDevices.values) {
            if (device.vendorId == 0x1a86 && device.productId == 0x7523) {
                startSerialConnection(usbManager, device)
                break
            }
        }

        activity.registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        updateDisplay()
    }

    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                // TODO this is for demo only
                //randomizeConfiguration()

                updateDisplay()
            }
        }
    }

    private val random = Random()
    private fun randomizeConfiguration() {
        val colors = activity.resources.getIntArray(R.array.palette_colors)
        val randomColor = colors[random.nextInt(colors.size)]
        val newConfiguration = Configuration(randomColor)
        configurationPersister.saveConfiguration(newConfiguration)
    }

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
            updateDisplay()
        }
    }

    fun updateConfiguration(newConfiguration: Configuration) {
        configurationPersister.saveConfiguration(newConfiguration)
        updateDisplay()
    }

    private fun updateDisplay() {
        val now = LocalDateTime(DateTimeZone.forID("Europe/Rome"))  // TODO the user should be able to change this
        val matrix = if (now.year < 2016) matrixDisplayer.createErrorMatrix() else matrixDisplayer.createMatrix(now.toLocalTime())
        val matrixStr = matrixSerialiser.serialise(matrix, configurationPersister.lastSavedConfiguration())
        serialConnection?.write(matrixStr.toByteArray(Charset.forName("UTF-8")))
    }

    fun stopPresenting() {
        activity.unregisterReceiver(tickReceiver)
        serialConnection?.close()
    }
}
