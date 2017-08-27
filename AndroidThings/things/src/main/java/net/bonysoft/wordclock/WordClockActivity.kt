package net.bonysoft.wordclock

import android.app.Activity
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import timber.log.Timber
import java.nio.charset.Charset


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class WordClockActivity : Activity() {

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

    private fun startSerialConnection(usbManager: UsbManager, device: UsbDevice) {
        val connection = usbManager.openDevice(device)
        val serial = UsbSerialDevice.createUsbSerialDevice(device, connection)

        if (serial != null && serial.open()) {
            serial.setBaudRate(115200)
            serial.setDataBits(UsbSerialInterface.DATA_BITS_8)
            serial.setStopBits(UsbSerialInterface.STOP_BITS_1)
            serial.setParity(UsbSerialInterface.PARITY_NONE)
            serial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
            serial.read(object : UsbSerialInterface.UsbReadCallback {
                override fun onReceivedData(data: ByteArray) {
                    val dataStr = String(data, Charset.forName("UTF-8"))
                    Timber.d("Received $dataStr")
                }
            })
        }
    }


    override fun onStop() {
        configPresenter.stopPresenting()
        super.onStop()
    }
}
