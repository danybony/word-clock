package net.bonysoft.wordclock

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.bonysoft.wordclock.common.Configuration
import net.bonysoft.wordclock.leds.LedsDisplayer
import net.bonysoft.wordclock.leds.LedsDisplayerFactory
import net.bonysoft.wordclock.matrix.MatrixGenerator
import net.bonysoft.wordclock.matrix.MatrixGeneratorFactory
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import timber.log.Timber

class WordClockActivity : Activity() {

    companion object {
        private val FIREBASE_CLOCK = "clock"
        private val FIREBASE_CLOCK_BRIGHTNESS = "brightness"
        private val FIREBASE_CLOCK_RGB = "spectrumRGB"
    }

    private lateinit var nearbyConfigPresenter: ConfigPresenter
    private lateinit var ledDisplayer: LedsDisplayer
    private lateinit var matrixGenerator: MatrixGenerator
    private lateinit var configurationPersister: ConfigurationPersister

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configurationPersister = ConfigurationPersister(PreferenceManager.getDefaultSharedPreferences(this))

        matrixGenerator = MatrixGeneratorFactory().createGenerator()
        ledDisplayer = LedsDisplayerFactory().createDisplayer(this)
        val database = FirebaseDatabase.getInstance().reference.child(FIREBASE_CLOCK)
        database.addValueEventListener(firebaseChangedEventListener)
        nearbyConfigPresenter = ConfigPresenter(this,
                getString(R.string.app_name),
                getString(R.string.service_id),
                configurationPersister,
                this::updateConfiguration
        )
    }

    private val firebaseChangedEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val spectrumRGB = snapshot.child(FIREBASE_CLOCK_RGB).getValue(Int::class.java) ?: 0
            val brightness = snapshot.child(FIREBASE_CLOCK_BRIGHTNESS).getValue(Int::class.java) ?: 100
            Timber.d("onDataChange (spectrum=$spectrumRGB), brightness=$brightness)")
            updateConfiguration(Configuration(spectrumRGB, brightness))
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.w("onCancelled", error.toException())
        }
    }

    private fun updateConfiguration(configuration: Configuration) {
        configurationPersister.saveConfiguration(configuration)
        displayCurrentTime()
    }

    override fun onStart() {
        super.onStart()
        nearbyConfigPresenter.startPresenting()
        ledDisplayer.start()
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        displayCurrentTime()
    }

    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.compareTo(Intent.ACTION_TIME_TICK) == 0) {
                displayCurrentTime()
            }
        }
    }

    private fun displayCurrentTime() {
        val now = LocalDateTime(DateTimeZone.forID("Europe/Rome"))  // TODO the user should be able to change this
        val matrix = if (now.year < 2016) matrixGenerator.createErrorMatrix() else matrixGenerator.createMatrix(now.toLocalTime())
        val configuration = configurationPersister.lastSavedConfiguration()
        ledDisplayer.display(matrix, configuration)
    }

    override fun onStop() {
        unregisterReceiver(tickReceiver)
        nearbyConfigPresenter.stopPresenting()
        ledDisplayer.stop()
        super.onStop()
    }
}
