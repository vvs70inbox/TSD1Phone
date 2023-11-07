package ru.vvs.terminal1

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vvs.terminal1.databinding.ActivityMainBinding
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private var mBinding: ActivityMainBinding?= null
    private val binding get() = mBinding!!
    lateinit var navController: NavController
    lateinit var actionBar: ActionBar

    lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivity = this
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        actionBar = supportActionBar!!

        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()
        moduleInstall
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.
                    Log.d("MLKit", "Module Loaded and Installed")
                }
            }
            .addOnFailureListener {
                // Handle failure…
                Log.d("MLKit", "Module Not Loaded")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    companion object {
        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true //pingHost("91.230.197.241")
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true //pingHost("91.230.197.241")
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true //pingHost("91.230.197.241")
                    }
                }
            }
            return false
        }
        private fun pingHost(ipAddress: String): Boolean {
            //val ipAddress = "www.google.com"
/*            try {
                val inet = InetAddress.getByName(ipAddress)
                return inet.isReachable(1000)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false*/
            return try {
                val sock = Socket()
                sock.connect(InetSocketAddress("91.230.197.241", 81), 1500)
                sock.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }
}