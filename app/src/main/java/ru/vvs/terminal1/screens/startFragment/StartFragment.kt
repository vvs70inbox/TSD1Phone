package ru.vvs.terminal1.screens.startFragment

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vvs.terminal1.MainActivity
import ru.vvs.terminal1.R
import ru.vvs.terminal1.Utils
import ru.vvs.terminal1.databinding.FragmentStartBinding
import ru.vvs.terminal1.mainActivity
import java.net.URL

class StartFragment : Fragment() {

    private var mBinding: FragmentStartBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStartBinding.inflate(inflater, container, false)

        binding.buttonList.setOnClickListener {
            (activity as MainActivity).navController.navigate(R.id.action_startFragment_to_mainFragment)
        }

        binding.buttonOrders.setOnClickListener {
            (activity as MainActivity).navController.navigate(R.id.action_startFragment_to_ordersFragment)
        }

        binding.buttonControls.setOnClickListener {
            (activity as MainActivity).navController.navigate(R.id.action_startFragment_to_salesFragment)
        }

        binding.appVersions.text = buildString {
            append(resources.getString(R.string.version))
            append(" ")
            append(MainActivity.APP_VERSION)
        }

        binding.buttonDownload.setOnClickListener {}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!MainActivity.appUpdate) {
            val s = "http://91.230.197.241/terminal/version."
            val sAPK = "http://91.230.197.241/terminal/tsd1phone.apk"
            val slash = "/"

            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val txt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        URL(s).openStream().readAllBytes().decodeToString()
                    } else {
                        "0.0.0"
                    }
                    if (MainActivity.APP_VERSION.replace(".","").toInt() < txt.replace(".","").toInt()) {
                        withContext(Dispatchers.Main) {
                            binding.buttonDownload.visibility = View.VISIBLE
                            binding.buttonDownload.setOnClickListener {
                                // check storage permission granted if yes then start downloading file
                                MainActivity.appUpdate = true
                                binding.buttonDownload.visibility = View.GONE
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (!Utils.isFileExists(sAPK)) {
                                        withContext(Dispatchers.IO) {
                                            Toast.makeText(
                                                mainActivity,
                                                R.string.hint,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        return@launch
                                    }
                                }.start()
                                val path = sAPK.split("/")
                                Utils.downloadFile(
                                    path[path.size - 1], sAPK, requireContext(), {
                                        Toast.makeText(
                                            requireActivity(),
                                            R.string.downloaded,
                                            Toast.LENGTH_LONG
                                        ).show()

                                        Utils.installAPK(
                                            "${
                                                Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_DOWNLOADS
                                                )
                                            }$slash${path[path.size - 1]}",
                                            requireContext()
                                        )
                                    })
                            }
                        }
                    }
                    else
                        MainActivity.appUpdate = true
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка интернета: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.actionBar.title = "Работа на телефоне"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
}