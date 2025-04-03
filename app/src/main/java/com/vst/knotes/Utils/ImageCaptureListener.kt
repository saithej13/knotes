package com.vst.knotes.Utils

import android.graphics.Bitmap

interface ImageCaptureListener {
    fun onImageCaptured(bitmap: Bitmap, imagePath: String)
}