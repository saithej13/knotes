package com.vst.knotes.MVVM.View

import android.R.attr.bitmap
import android.R.attr.height
import android.R.attr.width
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.View.Dashboard.dashboard
import com.vst.knotes.MVVM.View.menu.menu
import com.vst.knotes.MVVM.View.profile.profile
import com.vst.knotes.MVVM.View.support.support
import com.vst.knotes.R
import com.vst.knotes.Utils.FileUtils
import com.vst.knotes.Utils.ImageCaptureListener
import com.vst.knotes.databinding.BaseActivityBinding
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.IOException
import kotlin.math.roundToInt
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.support.common.FileUtil
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.MappedByteBuffer
import java.io.FileInputStream
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder


open class BaseActivity:AppCompatActivity() , FileUtils.DownloadListner {
    private lateinit var binding: BaseActivityBinding
    private var tflite: Interpreter? = null
//    private var inputBuffer: TensorBuffer? = null
//    private var outputBuffer: TensorBuffer? = null
    private var camera_imagepath = ""
    val CAMERA_CAPTURE_IMAGE_REQUEST_CODE: Int = 100
    val SELECT_IMAGE_REQUEST_CODE: Int = 101
    val num_classes = 26
    //    var ScreenTitle:String =""
    var _screenTitle = MutableLiveData<String>()
    var screenTitle: LiveData<String> = _screenTitle

    // Function to preprocess image and convert it to ByteBuffer
//    fun preprocessImage(original: Bitmap): ByteBuffer {
//        val resizedBitmap = Bitmap.createScaledBitmap(original, height, width, true)
//        val grayscaleBitmap = convertToGrayscale(resizedBitmap)
//
//        val byteBuffer = ByteBuffer.allocateDirect(4 * height * width)  // 4 bytes for FLOAT32
//        byteBuffer.order(ByteOrder.nativeOrder())  // Set byte order to native order
//
//        for (y in 0 until height) {
//            for (x in 0 until width) {
//                val pixel = grayscaleBitmap.getPixel(x, y)
//                val normalizedPixel = Color.red(pixel) / 255.0f  // Normalize to [0, 1]
//                byteBuffer.putFloat(normalizedPixel)
//            }
//        }
//        return byteBuffer
//    }

    fun preprocessImage(original: Bitmap): ByteBuffer {
        // Resize the image to match the expected input shape (e.g., 28x28 for grayscale)
        val resizedBitmap = Bitmap.createScaledBitmap(original, 28, 28, true)
        val grayscaleBitmap = convertToGrayscale(resizedBitmap)

        // Allocate the ByteBuffer based on the expected size
        val byteBuffer = ByteBuffer.allocateDirect(4 * 28 * 28)  // 4 bytes for each float
        byteBuffer.order(ByteOrder.nativeOrder())  // Set byte order to native order

        // Normalize the pixel values to [0, 1] and populate the buffer
        for (y in 0 until 28) {
            for (x in 0 until 28) {
                val pixel = grayscaleBitmap.getPixel(x, y)
                val normalizedPixel = Color.red(pixel) / 255.0f  // Normalize to [0, 1]
                byteBuffer.putFloat(normalizedPixel)
            }
        }

        return byteBuffer
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "vibration_channel"
            val channelName = "Vibration Service Channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Channel for vibration service notifications"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    // Function to load the model, run inference, and process the result
    fun runInference(bitmap: Bitmap) {
        try {
            val tflite = Interpreter(loadModelFile())
            Log.d("Model_Load", "Loaded successfully")

            // Ensure the dimensions match what the model expects
            val inputBuffer = preprocessImage(bitmap)
            Log.d("inputBuffer", "inputBuffer successfully")

            val outputBuffer = Array(1) { FloatArray(num_classes) }
            Log.d("outputBuffer", "outputBuffer successfully")

            tflite.run(inputBuffer, outputBuffer)
            Log.d("tflite.run", "tflite.run successfully")

            val recognizedText = processOutput(outputBuffer)
            Log.d("Recognized Text", recognizedText)
        } catch (e: Exception) {
            Log.d("Exception e", "" + e)
        }
    }
    fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = assets.openFd("handwritten_text_model.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        println("Model loading...")
        val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        println("Model loaded successfully!")
        return modelBuffer
    }

    fun createInputBuffer(): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * height * width)  // Adjust size according to the model input
        buffer.order(ByteOrder.nativeOrder())
        return buffer
    }

    // Function to process the output from the model
    fun processOutput(outputBuffer: Array<FloatArray>): String {
        var recognizedText = ""
        val outputArray = outputBuffer[0]

        // Set how many top predictions you want
        val topN = 10

        // Create a list of (index, value) pairs and sort them by probability (descending)
        val sortedPredictions = outputArray
            .mapIndexed { index, value -> index to value }
            .sortedByDescending { it.second }

        // Extract the top N predictions
        for (i in 0 until topN) {
            val index = sortedPredictions[i].first
            val character = mapIndexToCharacter(index)
            recognizedText += character
        }

        return recognizedText
    }
    fun mapIndexToCharacter(index: Int): String {
        if (index == -1) return ""
        val characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return characterSet.getOrElse(index) { ' ' }.toString()
    }


    private var imageCaptureListener: ImageCaptureListener? = null
    companion object {
            val cartProducts: ArrayList<productModel> = ArrayList()
//            val schemeArrayList: ArrayList<scheme> = ArrayList()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BaseActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _screenTitle.value = ""
        screenTitle.observe(this, Observer { title ->
            supportActionBar?.title = title
        })
        if (savedInstanceState == null) {
            replaceFragment(dashboard())
        }
        setupBottomNavigation()
//        if (savedInstanceState == null) {
//            replaceFragment(HomeFragment())
//        }
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
    fun replaceFragment(fragment: Fragment) {
        if (fragment is menu || fragment is dashboard || fragment is support || fragment is profile) {
            _screenTitle.value = fragment.javaClass.simpleName
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace container with the fragment
                .commit()
        }
        else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace container with the fragment
                .addToBackStack(null) // Allows back navigation
                .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null) {
//                    /Log.d("currentFragment",""+currentFragment.javaClass.simpleName)
                _screenTitle.value = currentFragment.javaClass.simpleName
            }
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.bottomNavigation.visibility = View.VISIBLE
            } else {
                if (currentFragment is dashboard || currentFragment is menu|| currentFragment is support || currentFragment is profile) {
                    binding.bottomNavigation.visibility = View.VISIBLE
                } else {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
    private fun setupBottomNavigation() {
        binding.bottomNavigation.visibility=View.VISIBLE

        binding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home ->{
                    replaceFragment(dashboard())
                    true
                }
                R.id.nav_menu -> {
                    replaceFragment(menu())
                    true
                }
                R.id.nav_support -> {
                    replaceFragment(support())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(profile())
                    true
                }
                else -> false
            }
        }
    }
    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }
    override fun onBackPressed() {
//        super.onBackPressed()// do not enable this , if enabled it will automaically closes the application
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        else if (supportFragmentManager.backStackEntryCount == 0) {
            showCustomDialog(
                title = "Logout",
                message = "Are you sure you want to LogOut?",
                "Yes", "No",
                onYesClick = {
                    // Handle Yes action
//                    super.onBackPressed()
                    finishAffinity()
                },
                onNoClick = {
                    // Handle No action

                }
            );
        }
        if (this is Login) {
            showCustomDialog(
                title = "Logout",
                message = "Are you sure you want to Exit",
                "Yes", "No",
                onYesClick = {
                    // Handle Yes action
//                    super.onBackPressed()
                    finishAffinity()
                },
                onNoClick = {
                    // Handle No action
                }
            )
        }
    }
    fun showCustomDialog(
        title: String,
        message: String,
        yesButtonText: String,
        noButtonText: String?,
        onYesClick: (() -> Unit)? = null,
        onNoClick: (() -> Unit)? = null
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        val tvTitlePopup = dialogView.findViewById<TextView>(R.id.tvTitlePopup)
        val tvMessagePopup = dialogView.findViewById<TextView>(R.id.tvMessagePopup)
        val btnNoPopup = dialogView.findViewById<Button>(R.id.btnNoPopup)
        val btnYesPopup = dialogView.findViewById<Button>(R.id.btnYesPopup)
        tvTitlePopup.text = title
        tvMessagePopup.text = message
        btnYesPopup.text = yesButtonText
        btnNoPopup.text = noButtonText
        if (onNoClick == null||noButtonText.equals("")) {
            btnNoPopup.visibility = View.GONE
        } else {
            btnNoPopup.visibility = View.VISIBLE
            btnNoPopup.setOnClickListener {
                onNoClick()
                dialog.dismiss()
            }
        }
        btnYesPopup.setOnClickListener {
            onYesClick?.invoke()
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Transparent BG
        dialog.show()
    }
    fun isDeviceSupportCamera(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    fun captureImage(imageFolder: String, listener: ImageCaptureListener) {
        if (isDeviceSupportCamera()) {

            val packageName = this@BaseActivity?.applicationContext?.packageName
            val file: File? = FileUtils.getOutputImageFile("" + imageFolder)
            if (file != null) {
                camera_imagepath = file.absolutePath
                Log.d("Camera", "File path: $camera_imagepath")
                var fileUri: Uri? = null
                try {
                    fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.d("Camera", "SDK_INT_M")
                        FileProvider.getUriForFile(this, "$packageName.provider", file)
                    } else {
                        Log.d("Camera", "SDK")
                        Uri.fromFile(file)
                    }
                    Log.d("Camera", "File URI: $fileUri")
                } catch (e: Exception) {
                    Log.e("Camera", "Error creating file URI: $e")
                }

            if (fileUri != null) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                    intent.putExtra("fileName", file.name)
                    intent.putExtra("filePath", file.absolutePath)
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
                } else {
                    Log.e("Camera", "Failed to create file URI")
                }
            } else {
                Log.e("Camera", "Failed to create file")
            }
        } else {
            Log.e("Camera", "Device does not support camera")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val selectedImageUri = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                imageCaptureListener?.onImageCaptured(bitmap, selectedImageUri.toString())
            }


        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val file = File(camera_imagepath)
                if (!file.exists()) return
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val resizedBitmap = resizeBitmap(bitmap, 720, 1280)
                val path = getRealPathFromURI(Uri.fromFile(file))
                val rotatedBitmap = rotateImage(resizedBitmap, path+"")
                imageCaptureListener?.onImageCaptured(rotatedBitmap, camera_imagepath)
            }
        }
    }

    fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        val ei = ExifInterface(path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }
    fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getRealPathFromURI(uri: Uri?): String {
        if (uri == null) {
            return ""
        }
        var path = ""
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex > -1) {
                    path = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } else {
            path = uri.path!!
        }
        return path
    }
    fun setImageCaptureListener(listener: ImageCaptureListener) {
        imageCaptureListener = listener
    }
    fun removeImageCaptureListener() {
        imageCaptureListener = null
    }


    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val ratioX = width / bitmap.width.toFloat()
        val ratioY = height / bitmap.height.toFloat()
        val middleX = width / 2.0f
        val middleY = height / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bitmap, middleX - bitmap.width / 2, middleY - bitmap.height / 2, Paint())
        return scaledBitmap
    }


    fun showImageSelectionDialog(listener: ImageCaptureListener) {

        showCustomDialog(
            title = "Select Image",
            message = "Choose an option to select an image",
            "Capture Photo","Select from Storage",
            onYesClick = {
                captureImage("your_image_folder", listener)
            },
            onNoClick = {
                selectImageFromStorage(listener)
            }
        )

    }

    override fun onDestroy() {
        super.onDestroy()
//        tflite?.close();
    }
    fun preprocessImage(original: Bitmap?): Bitmap {
        val resizedBitmap = Bitmap.createScaledBitmap(original!!, height, width, true)
        val grayscaleBitmap = convertToGrayscale(resizedBitmap)
        return grayscaleBitmap
    }

    fun convertToGrayscale(original: Bitmap): Bitmap {
        val grayscaleBitmap =
            Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        paint.setColorFilter(ColorMatrixColorFilter(ColorMatrix()))
        canvas.drawBitmap(original, 0f, 0f, paint)
        return grayscaleBitmap
    }
    fun selectImageFromStorage(listener: ImageCaptureListener) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        // Call listener.onImageCaptured() when the image is selected
    }

}
