package com.vst.knotes.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vst.knotes.MVVM.Services.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile

private const val BUFFER_SIZE = 1024
private const val MAX_RETRIES = 3
private var count = 0
class FileUtils {

    interface DownloadListner {
        fun onProgress(count: Int)
        fun onComplete()
        fun onError()
    }
    companion object {
        private const val BUFFER_SIZE = 8192  // Define buffer size
        private var count = 0  // Track retries
        private const val MAX_RETRIES = 3
        private lateinit var preference: Preference
        suspend fun downloadSQLITE(
            downloadUrl: String?,
            downloadListner: DownloadListner?,
            context: Context
        ): Boolean {
            if (downloadListner == null) {
                throw NullPointerException("downloadListner cannot be null")
            }
            preference = Preference(context)
//            val strFile = downloadSQLITEFile(downloadUrl, AppConstants.DATABASE_PATH, context, "knotes", downloadListner)
            val strFile = downloadFileToAppPath(context, downloadUrl+"", "knotes.zip",  downloadListner)
            return strFile != null
        }

        @Synchronized
        fun downloadSQLITEFile(
            sUrl: String?,
            directory: String,
            context: Context,
            sqliteName: String,
            downloadListener: DownloadListner
        ): String? {
            var sUrl = sUrl ?: return null
            if (!sUrl.contains(".zip")) {
                sUrl = sUrl.replace("sqlite", "zip")
            }

            val localFilePath = "$sqliteName.zip"
            val file = File(directory, localFilePath)
            if (file.exists()) {
                file.delete()
            }

            val bytes = ByteArray(BUFFER_SIZE)
            var urlConnection: HttpURLConnection? = null
            var localFile: String? = null
            var fileOutputStream: FileOutputStream? = null
            var bufferedOutputStream: BufferedOutputStream? = null
            var inputStream: InputStream? = null

            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::DownloadWakelock")

            try {
                wakeLock.acquire()

                sUrl = sUrl.replace(" ", "%20")
                val url = URL(sUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 10000
                urlConnection.readTimeout = 10000
                urlConnection.connect()

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val fileSize = urlConnection.contentLength
                    inputStream = urlConnection.inputStream

                    val directoryFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    directoryFile?.mkdirs()

                    val downloadedFile = File(directoryFile, localFilePath)
                    localFile = downloadedFile.absolutePath
                    fileOutputStream = FileOutputStream(localFile)
                    bufferedOutputStream = BufferedOutputStream(fileOutputStream)

                    var bytesReadTotal = 0L
                    var bytesRead: Int
                    while (inputStream.read(bytes, 0, BUFFER_SIZE).also { bytesRead = it } > 0) {
                        bufferedOutputStream.write(bytes, 0, bytesRead)
                        bytesReadTotal += bytesRead

                        if (fileSize > 0) {
                            val downloadPercentage = ((bytesReadTotal * 100.0f) / fileSize).toInt()
                            downloadListener.onProgress(downloadPercentage)
                        }
                    }

                    bufferedOutputStream.close()
                    fileOutputStream.close()
                    inputStream.close()

                    downloadListener.onComplete()
                } else {
                    Log.e("Download", "Server error: ${urlConnection.responseCode}")
                    return null
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Download failed", e)
                file.delete()

                if (count < MAX_RETRIES) {
                    count++
                    urlConnection?.disconnect()  // ? Close connection before retrying
                    return downloadSQLITEFile(sUrl, directory, context, sqliteName, downloadListener)
                } else {
                    downloadListener.onError()
                    return null
                }
            } finally {
                wakeLock.release()
                urlConnection?.disconnect()
            }
            return localFile
        }

        suspend fun downloadFileToAppPath(
            context: Context,
            fileUrl: String,
            fileName: String,
            downloadListener: DownloadListner
        ): File? {
            return withContext(Dispatchers.IO) {
                try {

                    val client = OkHttpClient()
                    val request = Request.Builder().url(fileUrl).build()
                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) {
                        Log.e("DownloadFile", "Failed: ${response.code}")
                        return@withContext null
                    }
                    val inputStream: InputStream? = response.body?.byteStream()
                    val fileSize = response.body?.contentLength()?: -1
                    if (inputStream == null) {
                        Log.e("DownloadFile", "InputStream is null")
                        return@withContext null
                    }
                    val file = File(context.filesDir, fileName)
                    FileOutputStream(file).use { outputStream ->
                        inputStream.use { input ->
                            val buffer = ByteArray(8192) // 8KB buffer size
                            var bytesRead: Int
                            var bytesReadTotal = 0L

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                                bytesReadTotal += bytesRead

                                if (fileSize > 0) {
                                    val downloadPercentage = ((bytesReadTotal * 100.0f) / fileSize).toInt()
                                    downloadListener.onProgress(downloadPercentage)
                                }
                            }
                            outputStream.flush()
                        }
                    }
                    upZipFile(file,file.absolutePath+"")
                    downloadListener.onComplete()
                    Log.d("DownloadFile", "File saved: ${file.absolutePath}")
                    return@withContext file
                } catch (e: Exception) {
                    Log.e("DownloadFile", "Error: ${e.message}")
                    downloadListener.onError()
                    null
                }
            }
        }
        val BUFF_SIZE: Int = 1024 * 1024
        @Throws(ZipException::class, IOException::class)
        fun upZipFile(zipFile: File?, folderPath: String) {
            val desDir = File(folderPath)
            if (!desDir.exists()) {
                desDir.mkdirs()
            }
            val zf = ZipFile(zipFile)
            val buffer = ByteArray(BUFF_SIZE)
            val entries: Enumeration<*> = zf.entries()
            while (entries.hasMoreElements()) {
                val entry = (entries.nextElement() as ZipEntry)
                val `in` = zf.getInputStream(entry)
                var newStr=""
                if (folderPath.length > 10) {
                    newStr = folderPath.substring(0, folderPath.length - 10)
                    preference.saveStringInPreference(SQLITEFILEPATH, ""+newStr)
                    preference.commitPreference()
                }
                var str = newStr + entry.name
                str = String(str.toByteArray(charset("8859_1")), charset("GB2312"))
                val desFile = File(str)
                if (!desFile.exists()) {
                    val fileParentDir = desFile.parentFile
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs()
                    }
                    desFile.createNewFile()
                }
                val out: OutputStream = FileOutputStream(desFile)
                var realLength: Int
                while ((`in`.read(buffer).also { realLength = it }) > 0) {
                    out.write(buffer, 0, realLength)
                }
                val filename = File(folderPath, AppConstants.DATABASE_NAME+"")
                if (desFile.exists()) desFile.renameTo(filename)
                `in`.close()
                out.close()
            }
        }

        fun getOutputImageFile(folder: String): File? {
            val captureImagesStorageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/knotes/" + folder
            )

            if (!captureImagesStorageDir.exists()) {
                if (!captureImagesStorageDir.mkdirs()) {
                    Log.d("Unilever", "Oops! Failed create ")
                    return null
                }
            }

            val timestamp = System.currentTimeMillis().toString() + ""
            val imageFile = File(
                captureImagesStorageDir.path + File.separator
                        + "CAPTURE_" + timestamp + ".jpg"
            )


            return imageFile
        }
    }
//    companion object {
//        fun downloadSQLITE(
//            downloadUrl: String?,
//            downloadListner: DownloadListner?,
//            context: Context
//        ): Boolean {
//            if (downloadListner == null) {
//                throw NullPointerException("downloadListner cannot be null")
//            }
//            val strFile = downloadSQLITEFile(downloadUrl, AppConstants.DATABASE_PATH, context, "knotes", downloadListner)
//            return strFile != null
//        }
//        @Synchronized
//        fun downloadSQLITEFile(
//            sUrl: String?,
//            directory: String,
//            context: Context,
//            sqliteName: String,
//            downloadListener: DownloadListner
//        ): String? {
//            var sUrl = sUrl
//            if (!sUrl!!.contains(".zip")) {
//                sUrl = sUrl.replace("sqlite", "zip")
//            }
//
//            val localFilePath = "$sqliteName.zip"
//            val file = File(directory, localFilePath)
//            if (file.exists()) {
//                file.delete()
//            }
//            var fileSize = 0
//            var bytesRead = 0
//            val bytes = ByteArray(BUFFER_SIZE)
//            var inputStream: InputStream? = null
//            var urlConnection: HttpURLConnection? = null
//            var localFile: String? = null
//            val fileOutputStream: FileOutputStream
//            var bufferedOutputStream: BufferedOutputStream? = null
//            var downloadPercentage = 0
//
//            try {
//                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
////                val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
//                val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::DownloadWakelock")
//
//                try {
//                    wakeLock.acquire()  // Ensure WakeLock is acquired before using
//                    // Download logic...
//                    sUrl = sUrl.replace(" ", "%20")
//                    val url = URL(sUrl)
//                    urlConnection = url.openConnection() as HttpURLConnection
//                    urlConnection.connectTimeout = 10000
//                    urlConnection.readTimeout = 10000
//
//                    urlConnection.connect()
//                    val responseCode = urlConnection.responseCode
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        fileSize = urlConnection.contentLength
//                        inputStream = urlConnection.inputStream
//
////                val fileSize = urlConnection.contentLength
////                inputStream = urlConnection.inputStream
////                inputStream = new BufferedInputStream(urlConnection.getInputStream());
////                    val directoryFile = File(directory)
////                    val directoryFile = Environment.getExternalStorageDirectory()
//                        val directoryFile =
//                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//                        if (directoryFile != null) {
//                            if (!directoryFile.exists() || !directoryFile.isDirectory) {
//                                directoryFile.mkdirs()
//                            }
//                        }
//
//                        val file = File(directoryFile, localFilePath)
//                        localFile = file.absolutePath
//                        fileOutputStream = FileOutputStream(localFile)
//                        bufferedOutputStream = BufferedOutputStream(fileOutputStream)
//
//                        var bytesReadTotal = 0L
//                        do {
//                            bytesRead = inputStream.read(bytes, 0, BUFFER_SIZE)
//                            if (bytesRead > 0) {
//                                bufferedOutputStream.write(bytes, 0, bytesRead)
//                                bufferedOutputStream.flush()
//                            }
//                            bytesReadTotal += bytesRead
////                    downloadPercentage = ((bytesReadTotal * 100) / fileSize).toInt()
////                    downloadListener.onProgress(downloadPercentage)
//                            if (fileSize != 0) {
//                                downloadPercentage = ((bytesReadTotal * 100.0f) / fileSize).toInt()
//                                downloadListener.onProgress(downloadPercentage)
//                            }
//                        } while (bytesRead > 0)
//
//                        if (fileSize > file.length()) {
//                            file.delete()
//                            return null
//                        }
//
//                        // Unzip the file
//                        // ...
//
//                        bufferedOutputStream.close()
//                        fileOutputStream.close()
//                        inputStream.close()
//                        wakeLock.release()
//
//                        downloadListener.onComplete()
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    if (wakeLock.isHeld) {
//                        wakeLock.release()  // Release only if held
//                    }
//                }
//                return localFile
//            } catch (e: Exception) {
//                Log.d("EXEPTION ",""+e.printStackTrace())
//                file.delete()
//                if (count < MAX_RETRIES) {
//                    count++
//                    e.printStackTrace()
//                    return downloadSQLITEFile(sUrl, directory, context, sqliteName, downloadListener)
//                } else {
//                    downloadListener.onError()
//                    return null
//                }
//            }
//        }
//    }


    fun createDirectory(context: Context, directoryName: String): File? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and later, use the Scoped Storage API
            val directoryFile = context.getExternalFilesDir(null)
            if (directoryFile != null) {
                val file = File(directoryFile, directoryName)
                if (!file.exists()) {
                    file.mkdirs()
                }
                return file
            } else {
                Log.e("Error", "Failed to get external files directory")
                return null
            }
        } else {
            // For Android 10 and earlier, use the legacy storage API
            val directoryFile = Environment.getExternalStorageDirectory()
            val file = File(directoryFile, directoryName)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (!file.exists()) {
                    file.mkdirs()
                }
                return file
            } else {
                // Request the permission
                ActivityCompat.requestPermissions((context as Activity), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return null
            }
        }
    }










}