package com.vskyway

import android.app.Application
import com.vskyway.data.db.SkywayDatabase
import android.util.Log

class SkywayApp : Application() {

    // Database instance jo pure app mein available rahega
    val database: SkywayDatabase by lazy { SkywayDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        Log.d("SkywayApp", "System Initialized: Booting v. Skyway AI OS")
        
        // TODO: Part 6 mein hum yahan Keystore Master Key verification trigger karenge
    }
}