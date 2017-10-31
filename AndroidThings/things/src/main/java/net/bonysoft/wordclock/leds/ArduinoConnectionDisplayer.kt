package net.bonysoft.wordclock.leds

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import net.bonysoft.wordclock.MatrixSerialiser
import net.bonysoft.wordclock.common.Configuration
import timber.log.Timber
import java.nio.charset.Charset

class ArduinoConnectionDisplayer(private val usbManager: UsbManager,
                                 private val matrixSerialiser: MatrixSerialiser) : LedsDisplayer {

    private var serialConnection: UsbSerialDevice? = null

    override fun start() {
        val connectedDevices = usbManager.deviceList
        for (device in connectedDevices.values) {
            if (device.vendorId == 0x1a86 && device.productId == 0x7523) {
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
        }
    }

    override fun display(matrix: BooleanArray, configuration: Configuration) {
        val matrixStr = matrixSerialiser.serialise(matrix, configuration)
        serialConnection?.write(matrixStr.toByteArray(Charset.forName("UTF-8")))
    }

    override fun stop() {
        serialConnection?.close()
    }
}
