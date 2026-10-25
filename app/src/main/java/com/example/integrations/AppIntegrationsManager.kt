package com.example.integrations

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

object AppIntegrationsManager {
    private const val TAG = "AppIntegrations"

    /**
     * Launch or send message via WhatsApp
     */
    fun sendWhatsApp(context: Context, phoneNumber: String = "", message: String) {
        try {
            val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
            val encodedMessage = Uri.encode(message)

            val uri = if (cleanNumber.isNotBlank()) {
                Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMessage")
            } else {
                Uri.parse("https://api.whatsapp.com/send?text=$encodedMessage")
            }

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setPackage("com.whatsapp")
            }

            if (isPackageInstalled(context, "com.whatsapp")) {
                context.startActivity(intent)
            } else {
                // Try generic send or browser fallback
                val webIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp dispatch failed", e)
            openInPlayStore(context, "com.whatsapp", "WhatsApp")
        }
    }

    /**
     * Launch or send message via Facebook Messenger
     */
    fun sendMessenger(context: Context, message: String) {
        try {
            if (isPackageInstalled(context, "com.facebook.orca")) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    setPackage("com.facebook.orca")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(sendIntent)
            } else {
                // Web fallback
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.messenger.com/")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Messenger dispatch failed", e)
            openInPlayStore(context, "com.facebook.orca", "Facebook Messenger")
        }
    }

    /**
     * Launch or send message via Imo
     */
    fun sendImo(context: Context, message: String) {
        try {
            val imoPackage = "com.imo.android.imoim"
            if (isPackageInstalled(context, imoPackage)) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    setPackage(imoPackage)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(sendIntent)
            } else {
                // If not installed, open Imo in store
                openInPlayStore(context, imoPackage, "Imo Messenger")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Imo dispatch failed", e)
            openInPlayStore(context, "com.imo.android.imoim", "Imo")
        }
    }

    /**
     * Initiate phone call or open dialer with number
     */
    fun initiatePhoneCall(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace("[^0-9+]".toRegex(), "")
        val targetUri = Uri.parse("tel:${if (cleanNumber.isNotBlank()) cleanNumber else ""}")

        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        try {
            val intent = if (hasCallPermission && cleanNumber.isNotBlank()) {
                Intent(Intent.ACTION_CALL, targetUri)
            } else {
                Intent(Intent.ACTION_DIAL, targetUri)
            }.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initiate call", e)
            Toast.makeText(context, "Could not open phone dialer", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Stream music through YouTube
     */
    fun streamYouTube(context: Context, query: String) {
        val cleanQuery = query.ifBlank { "Trending music" }
        try {
            val youtubePkg = "com.google.android.youtube"
            if (isPackageInstalled(context, youtubePkg)) {
                val searchIntent = Intent(Intent.ACTION_SEARCH).apply {
                    setPackage(youtubePkg)
                    putExtra("query", cleanQuery)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(searchIntent)
            } else {
                val webUri = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(cleanQuery)}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stream YouTube", e)
            val webUri = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(cleanQuery)}")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
    }

    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openInPlayStore(context: Context, packageName: String, appName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
        }
        Toast.makeText(context, "$appName is not installed on this device", Toast.LENGTH_SHORT).show()
    }
}
