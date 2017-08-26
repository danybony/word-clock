package net.bonysoft.wordclock

import android.app.Application
import timber.log.Timber


class ClockConfigApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}
