package com.vst.knotes

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.vst.knotes.MVVM.Services.ProjectRepository
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.Login
import com.vst.knotes.MVVM.View.OnboardingActivity
import com.vst.knotes.Utils.FileUtils
import com.vst.knotes.Utils.FileUtils.Companion.downloadSQLITE
import com.vst.knotes.Utils.ISSQLITEDOWNLOADED
import com.vst.knotes.Utils.MilkApplicationCheck
import com.vst.knotes.Utils.NetworkManager
import com.vst.knotes.Utils.Preference
import com.vst.knotes.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class SplashScreenActivity: AppCompatActivity() , FileUtils.DownloadListner{
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var projectRepository: ProjectRepository
    var ISAPPFIRSTLAUNCH: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        projectRepository = ProjectRepository(application)
        val preference = Preference(this)

//        setContentView(R.layout.activity_splash_screen)
        NetworkManager.init(this)
        if (NetworkManager.isInternetAvailable(this)) {
            // Internet is available
            lifecycleScope.launch {
                val response = projectRepository.getServices()
                response.observe(this@SplashScreenActivity) { data ->
                    if (data != null) {
                        try {
                            val responsedata = Gson().fromJson(data, JsonObject::class.java)
//                            responsedata.addProperty("response",""+data.toString());

                            if (responsedata.has("knotes")) {
                                val knotesValue = responsedata.get("knotes").asString
                                val knotesDB = responsedata.get("knotesdb").asString
                                Log.d("TAG", knotesValue) // prints "MilkApplication"
                                if(knotesValue.equals("MilkApplicatio")) //MilkApplication
                                {
                                    preference.saveBooleanInPreference(MilkApplicationCheck, true);
                                    preference.commitPreference()
                                    val IsSQliteDownloaded = preference.getBooleanFromPreference(ISSQLITEDOWNLOADED, false)
                                    if(IsSQliteDownloaded)
                                    {

                                    }
                                    else{
                                        Thread {
                                            lifecycleScope.launch {
//                                            showDownloadProgressBar()
                                                val downloadUrl =
                                                    knotesDB+"" // Replace with a valid URL //https://raw.githubusercontent.com/saithej13/MyApplication/refs/heads/master/knotes.zip
                                                if (!downloadSQLITE(downloadUrl, this@SplashScreenActivity, this@SplashScreenActivity)) {
                                                    Log.d("SQLITE", "FALSE")
                                                    preference.saveBooleanInPreference(ISSQLITEDOWNLOADED, false)
                                                    preference.commitPreference()
                                                } else {
                                                    Log.d("SQLITE", "TRUE")
                                                    preference.saveBooleanInPreference(ISSQLITEDOWNLOADED, true)
                                                    preference.commitPreference()
                                                }
                                            }
                                        }.start()
                                    }
                                }
                            } else {
                                preference.saveBooleanInPreference(MilkApplicationCheck, false);
                                preference.commitPreference()
//                                Log.d("TAG", "knotes key does not exist")
                            }
                        } catch (e: Exception) {
//                            Log.d("TAG", "Error : $e")
                            preference.saveBooleanInPreference(MilkApplicationCheck, false);
                            preference.commitPreference()
                        }
                    }
                }
            }
        } else {
            // Internet is not available
        }
        // Delay for 3 seconds before moving to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            val MilkApplication = preference.getBooleanFromPreference(MilkApplicationCheck, false)
            if(MilkApplication)
            {
//                val intent = Intent(this, Login::class.java)
                val intent = Intent(this, BaseActivity::class.java)
//                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this, MainScreen::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }
    override fun onDestroy() {
        super.onDestroy()
        NetworkManager.unregisterReceiver(this)
    }

    override fun onProgress(count: Int) {
        runOnUiThread {
            if (dialogDownload != null) {
                progressBar!!.progress = count
                tvProgress!!.text = "$count %"
            }
        }
    }

    override fun onComplete() {
        runOnUiThread {
            if (dialogDownload != null && dialogDownload!!.isShowing) {
                dialogDownload!!.dismiss()
            }
        }
    }

    override fun onError() {
        runOnUiThread {
            if (dialogDownload != null && dialogDownload!!.isShowing) {
                dialogDownload!!.dismiss()
            }
        }
    }
    private var tvProgress: TextView? = null
    private var progressBar: ProgressBar? = null
    private var dialogDownload: Dialog? = null
    @SuppressLint("NewApi")
    private fun showDownloadProgressBar() {
        runOnUiThread {
            val inflater = LayoutInflater.from(this)
            val v: View = inflater.inflate(R.layout.progressdialog, null)
            progressBar = v.findViewById(R.id.prgbar) as ProgressBar
            tvProgress = v.findViewById(R.id.tvprogress) as TextView

            if (dialogDownload == null) {
                dialogDownload = Dialog(this)
                dialogDownload!!.setTitle("Downloading master data file...")
                dialogDownload!!.setCancelable(false)
            }

            val w = (getSharedPreferences("prefs", MODE_PRIVATE).getInt("device_display_width", 600) * (2f / 3f)).roundToInt()
            dialogDownload!!.setContentView(v, ViewGroup.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT))
            dialogDownload!!.window!!.setGravity(Gravity.CENTER)
            progressBar!!.max = 100
            progressBar!!.progress = 0
            tvProgress!!.text = "0 %"

            try {
                if (!isFinishing) {
                    dialogDownload!!.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}