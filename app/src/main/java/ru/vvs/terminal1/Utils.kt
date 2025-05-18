package ru.vvs.terminal1

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri
import java.io.File

class Utils {
    companion object {
        fun isFileExists(urlString: String): Boolean {
            var connection: HttpURLConnection? = null
            return try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 5000
                connection.connect()
                val responseCode = connection.responseCode
                responseCode == HttpURLConnection.HTTP_OK
            } catch (e: IOException) {
                e.printStackTrace()
                false
            } finally {
                connection?.disconnect()
            }
        }

        fun downloadFile(name: String, url: String, context: Context, onComplete: () -> Unit) {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(url.toUri())
            val slash = "/"
            val file = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}$slash$name")
            if (file.exists()) {
                file.delete()
            }

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle(name)
            request.setDescription(name)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

            var downloadId: Long = -1

            val onCompleteReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        onComplete()
                        context?.unregisterReceiver(this)
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                onCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS
            )

            downloadId = downloadManager.enqueue(request)

        }

        fun installAPK(filePath: String, context: Context) {
            // Create a File object for the file at the given path
            val file = File(filePath)

            // Check if the file exists
            if (file.exists()) {
                // Create an Intent to install the file
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    // Set URI and MIME type for the file
                    setDataAndType(
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider", // Specify the FileProvider authority
                            file
                        ),
                        "application/*" // MIME type for all file types
                    )
                    // Add a flag to grant read URI permission
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    // Add a flag to start a new task
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                // Start the activity to install the file
                context.startActivity(intent)
            } else {
                // Log an error if the file does not exist
                Log.e("Installer", "File does not exist: $filePath")
            }
        }
    }
}