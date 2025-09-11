package com.vst.knotes.utils

import android.graphics.Bitmap

interface ImageCaptureListener {
    fun onImageCaptured(bitmap: Bitmap, imagePath: String)
}