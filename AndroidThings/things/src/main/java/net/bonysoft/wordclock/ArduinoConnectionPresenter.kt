package net.bonysoft.wordclock

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Handler
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import timber.log.Timber
import java.nio.charset.Charset
import java.util.*

class ArduinoConnectionPresenter(val usbManager: UsbManager,
                                 val matrixDisplayer: MatrixGenerator,
                                 val matrixSerialiser: MatrixSerialiser,
                                 val handler: Handler) {

    private var serialConnection: UsbSerialDevice? = null

    fun startPresenting() {
        val connectedDevices = usbManager.deviceList
        for (device in connectedDevices.values) {
            if (device.getVendorId() === 0x1a86 && device.getProductId() === 0x7523) {
                Timber.d("Device found: " + device.deviceName)
                startSerialConnection(usbManager, device)
                break
            }
        }
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


    fun stopPresenting() {
        handler.removeCallbacks(triggerRunnable)
        serialConnection?.close()
    }
}
