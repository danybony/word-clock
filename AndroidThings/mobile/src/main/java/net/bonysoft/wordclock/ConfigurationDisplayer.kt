package net.bonysoft.wordclock

import android.view.View
import com.thebluealliance.spectrum.SpectrumPalette
import net.bonysoft.wordclock.common.Configuration

class ConfigurationDisplayer(rootView: View, onNewConfiguration: (Configuration) -> Unit) {

    private val configurationView = rootView.findViewById(R.id.configuration_view) as View
    private val paletteView = configurationView.findViewById(R.id.palette) as SpectrumPalette
    private val emptyView = rootView.findViewById(R.id.empty_view) as View

    init {
        paletteView.setOnColorSelectedListener {
            onNewConfiguration(Configuration(it))
        }
    }

    fun showConfiguration(configuration: Configuration) {
        paletteView.setSelectedColor(configuration.color)
        emptyView.visibility = View.GONE
        configurationView.visibility = View.VISIBLE
    }

    fun showNoDeviceAvailable() {
        emptyView.visibility = View.VISIBLE
        configurationView.visibility = View.GONE
    }
}
