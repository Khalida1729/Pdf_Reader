package com.kashif.pdfreader.function

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.kashif.pdfreader.data.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun renderPage(
    repository: PdfRepository,
    uri: Uri,
    index: Int
): Bitmap {

    return withContext(Dispatchers.IO) {
        val renderer = repository.openRenderer(uri)
        val page = renderer.openPage(index)

        val bitmap = createBitmap(
            page.width,
            page.height,
            Bitmap.Config.ARGB_8888
        )

        page.render(
            bitmap,
            null,
            null,
            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
        )

        page.close()
        renderer.close()
        bitmap
    }
}