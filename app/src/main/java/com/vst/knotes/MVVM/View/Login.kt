package com.vst.knotes.MVVM.View

import ScreenItem
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.JsonObject
import com.vst.knotes.MVVM.Services.ProjectRepository
import com.vst.knotes.MVVM.viewModel.LoginVM
import com.vst.knotes.R
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.Utils.FileUtils
import com.vst.knotes.Utils.MilkApplicationCheck
import com.vst.knotes.Utils.MyApplication
import com.vst.knotes.Utils.Preference
import com.vst.knotes.databinding.LoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class Login:BaseActivity() {
    private lateinit var binding: LoginBinding
    private lateinit var viewModel: LoginVM
    private lateinit var projectRepository: ProjectRepository
    private lateinit var screenItemList: MutableList<ScreenItem>
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val preference = Preference(this)
        projectRepository = ProjectRepository(application)
        binding.lifecycleOwner = this

        screenItemList = mutableListOf()
        val MilkApplication = preference.getBooleanFromPreference(MilkApplicationCheck, false)
        if(MilkApplication)
        {
            binding.txtwelcome.setText(baseContext.getString(R.string.welcome_back))
        }
        else{
            binding.txtwelcome.setText(baseContext.getString(R.string.welcome))
        }
        val payload = JsonObject()
        payload.addProperty("USERNAME", "USER1")
        payload.addProperty("PASSWORD", "1234")
        binding.btnSignIn.setOnClickListener{
            try {
                if(binding.edtUsername.text.isEmpty()){
                    showCustomDialog(
                        title = "Alert",
                        message = "Username Cannot be Empty",
                        "Ok","",{
                            //yes click
                        },{
                            //No click
                        }

                    )
                }
                else if(binding.edtpassword.text.isEmpty()){
                    showCustomDialog(
                        title = "Alert",
                        message = "Password Cannot be Empty",
                        "Ok","",{
                            //yes click
                        },{
                            //No click
                        }
                    )
                }
                else {
                    DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                        val query =
                            "SELECT * FROM tblUsers where username='" + binding.edtUsername.text + "' and password='" + binding.edtpassword.text + "'"
                        sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                do {
                                    val intent = Intent(this, OnboardingActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } while (cursor.moveToNext())
                            } else {
                                showCustomDialog(
                                    title = "Alert",
                                    message = "Unable to Login, Please try again",
                                    "Ok","",{
                                        //Yes Click
                                    },{
                                        //No Click
                                    }
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
            } finally {
                DatabaseHelper.closeDatabase()
            }
//            var strFile: Boolean = FileUtils.downloadSQLITE("", this, this@Login)
//            if(strFile){
//                Log.d("SQLITE","TRUE")
//            }
//            else{
//                Log.d("SQLITE","FALSE")
//            }
//            getPrintSlip()
//            preference.saveBooleanInPreference(MilkApplicationCheck, false);
//            preference.commitPreference()
//            lifecycleScope.launch {
//                val response = projectRepository.login(payload)
//
//                response.observe(this@Login) { data ->
//                    if (data != null) {
//                        try {
//                            val responsedata = Gson().fromJson(data, JsonObject::class.java)
////                            responsedata.addProperty("response",""+data.toString());
//
//                            if (responsedata.has("knotes")) {
//                                val knotesValue = responsedata.get("knotes").asString
//                                Log.d("TAG", knotesValue) // prints "MilkApplication"
//                            } else {
//                                Log.d("TAG", "knotes key does not exist")
//                            }
//                        } catch (e: Exception) {
//                            Log.d("TAG", "Error : $e")
//                        }
//                    }
//                }
//            }
        }
    }
//    fun getPrintSlip(): Bitmap {
//        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/PrinterTest")
//        myDir.mkdirs()
//
//        val n = 10000
//        var salesHeight = 1
//        var freeGoodsHeight = 1
//        var damageHeight = 1
//
//        val fname = "Image-$n.jpg"
//        val file = File(myDir, fname)
//        if (file.exists()) {
//            file.delete()
//        }
//
//        var bmp: Bitmap? = null
//        var out: FileOutputStream? = null
//        try {
//            out = FileOutputStream(file)
//            val width = 200
//            val height = 200 + ((salesHeight + freeGoodsHeight + damageHeight) * 30)
//            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//
//            val canvas = Canvas(bmp!!)
//            val paintBG = Paint()
//            paintBG.color = Color.WHITE
//            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBG)
//
//            var het1 = 40
//            var as1: Array<String>? = null
//            var Header: Array<String>? = null
//            var Headerh1 = 20 + 40
//
//            Header = arrayOf("Company Name", "Branch Name", "Collection CenterName", "Village Name", "Address", "Contact", "______________________________________________")
//            if (Header != null) {
//                for (i in Header.indices) {
//                    canvas.drawText(Header[i], ((width / 2) - (Header[i].length * 8)).toFloat(), Headerh1.toFloat(), getPaintObjHeaderNew(22))
//                    Headerh1 += 30
//                }
//            }
//            as1 = arrayOf(
//                "TDATE : TDATE",
//                "SHIFT : SHIFT",
//                "Farmer Code: FARMERID",
//                "Name: FARMERNAME",
//                "Milk Type: MILKTYPE",
//                "Milk Qty: QUANTITY",
//                "Fat: FAT",
//                "Snf: SNF",
//                "Rate: RATE",
//                "Amount: AMOUNT"
//            )
//            var h1 = 20 + Headerh1
//            if (as1 != null) {
//                for (i in as1.indices) {
//                    canvas.drawText(as1[i], 30f, h1.toFloat(), getPaintObjHeaderNew(16))
//                    h1 += 20
//                }
//            }
//
//            var Footer: Array<String>? = null
//            var Footerf1 = h1 + 20
//            Footer = arrayOf("______________________________________________")
//            if (Footer != null) {
//                for (i in Footer.indices) {
//                    canvas.drawText(Footer[i], 30f, Footerf1.toFloat(), getPaintObjHeaderNew(22))
//                    Footerf1 += 30
//                }
//            }
//
//            canvas.drawText(String.format("%12s", "Footer Text"), 300f, (Footerf1 + 10).toFloat(), getPaintObjHeaderNew(22))
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
//            out.flush()
//            out.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            bmp?.recycle()
//        }
//        return BitmapFactory.decodeFile(file.absolutePath)
//    }
    fun getPaintObjHeaderNew(size: Int): Paint {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = size.toFloat()
        return paint
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

    fun startDownload(downloadUrl: String) {
        runOnUiThread {
            progressDialog = ProgressDialog(this).apply {
                setMessage("Downloading Update, Please wait...")
                setIndeterminate(true)
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(false)
                show()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            downloadApk(downloadUrl)
        }
    }
    @SuppressLint("SuspiciousIndentation")
    private suspend fun downloadApk(urlString: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doOutput = true
                connection.connectTimeout = 15000  // Increased timeout
                connection.readTimeout = 15000
                connection.instanceFollowRedirects = true
                connection.connect()
                val responseCode = connection.responseCode
                        Log.e("UpdateAPP", "Response Code: $responseCode")
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned HTTP $responseCode")
                }
                val fileLength = connection.contentLength
//                val path = getExternalFilesDir(null)?.absolutePath ?: return@withContext
                val path = this@Login.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
                val file = File(path)
                if (!file.exists()) file.mkdirs()

                val outputFile = File(file, "knotes.zip")
                if (!outputFile.exists()) outputFile.createNewFile()

                val fos = FileOutputStream(outputFile)
                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArray(1024)
                var total: Long = 0
                var count: Int

                while (inputStream.read(buffer).also { count = it } != -1) {
                    total += count
                    fos.write(buffer, 0, count)
                    updateProgress(((total * 100) / fileLength).toInt())
                }

                fos.close()
                inputStream.close()

                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    Log.d("","File Downloaded")
//                    Toast.makeText( this,"", Toast.LENGTH_SHORT).show()
//                    installApk()
                }

            } catch (e: Exception) {
                Log.e("UpdateAPP", "Update error! ${e.message}")
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    Log.d("","Download error: ${e.message}")
//                    Toast.makeText( this,"", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateProgress(progress: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            progressDialog?.apply {
                isIndeterminate = false
                max = 100
                setProgress(progress)
            }
        }
    }

    private fun installApk() {
        try {
            val path = getExternalFilesDir(null)?.absolutePath ?: return
            val file = File("$path/my_apk.apk")
            val intent = Intent(Intent.ACTION_VIEW)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                val resInfoList: List<ResolveInfo> =
                    this.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                for (resolveInfo in resInfoList) {
                    this.grantUriPermission(
                        this.packageName + ".provider",
                        apkUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            this.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




}