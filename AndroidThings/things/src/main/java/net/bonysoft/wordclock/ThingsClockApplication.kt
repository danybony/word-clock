package net.bonysoft.wordclock

import android.app.Application
import timber.log.Timber

class ThingsClockApplication : Application()   {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}
