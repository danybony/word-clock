package net.bonysoft.wordclock

import android.content.SharedPreferences
import android.graphics.Color
import com.google.gson.Gson
import net.bonysoft.wordclock.common.Configuration

class ConfigurationPersister(val preferences: SharedPreferences) {

    val gson = Gson()

    fun saveConfiguration(configuration: Configuration) {
        preferences.edit()
                .putString("last_configuration", gson.toJson(configuration))
                .apply()
    }

    fun lastSavedConfiguration(): Configuration {
        val defaultConfigurationString = gson.toJson(Configuration(Color.CYAN))
        return gson.fromJson(preferences.getString("last_configuration", defaultConfigurationString), Configuration::class.java)
    }
}
