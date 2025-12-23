package com.kashif.pdfreader.data

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri

class PdfRepository(
    private val context: Context
) {

    fun openRenderer(uri: Uri): PdfRenderer {
        val pfd = context.contentResolver
            .openFileDescriptor(uri, "r")
            ?: throw IllegalStateException("Cannot open PDF")

        return PdfRenderer(pfd)
    }
}