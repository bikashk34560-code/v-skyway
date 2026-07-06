package com.vskyway.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.util.Log

class SkywayDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object { private const val TAG = "SkywayDeviceAdmin" }

    override fun onEnabled(context: Context, intent: Intent) {
        Log.i(TAG, "Device owner active")
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(PackageInstaller.EXTRA_STATUS)) {
            when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)) {
                PackageInstaller.STATUS_SUCCESS -> Log.i(TAG, "Silent install SUCCESS")
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    val confirm = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
                    confirm?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    confirm?.let { context.startActivity(it) }
                }
                else -> Log.e(TAG, "Silent install FAILED: status=$status")
            }
            return
        }
        super.onReceive(context, intent)
    }
}
