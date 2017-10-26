package net.bonysoft.wordclock

import android.app.Activity
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.bonysoft.wordclock.common.Configuration
import net.bonysoft.wordclock.matrix.MatrixGeneratorFactory
import timber.log.Timber

class WordClockActivity : Activity() {

    companion object {
        private val FIREBASE_CLOCK = "clock"
        private val FIREBASE_CLOCK_BRIGHTNESS = "brightness"
        private val FIREBASE_CLOCK_RGB = "spectrumRGB"
    }

    private lateinit var configPresenter: ConfigPresenter
    private lateinit var arduinoConnectionPresenter: ArduinoConnectionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configurationPersister = ConfigurationPersister(PreferenceManager.getDefaultSharedPreferences(this))
        val matrixGenerator = MatrixGeneratorFactory().createGenerator()
        arduinoConnectionPresenter = ArduinoConnectionPresenter(
                this,
                getSystemService<UsbManager>(UsbManager::class.java),
                matrixGenerator,
                MatrixSerialiser(),
                configurationPersister
        )
        val database = FirebaseDatabase.getInstance().reference.child(FIREBASE_CLOCK)
        database.addValueEventListener(lightChangedEventListener)
        configPresenter = ConfigPresenter(this,
                getString(R.string.app_name),
                getString(R.string.service_id),
                configurationPersister,
                arduinoConnectionPresenter::updateConfiguration
        )
    }

    private val lightChangedEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val spectrumRGB = snapshot.child(FIREBASE_CLOCK_RGB).getValue(Int::class.java) ?: 0
            val brightness = snapshot.child(FIREBASE_CLOCK_BRIGHTNESS).getValue(Int::class.java) ?: 100
            Timber.d("onDataChange (spectrum=$spectrumRGB), brightness=$brightness)")
            arduinoConnectionPresenter.updateConfiguration(Configuration(spectrumRGB, brightness))
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.w("onCancelled", error.toException())
        }
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
