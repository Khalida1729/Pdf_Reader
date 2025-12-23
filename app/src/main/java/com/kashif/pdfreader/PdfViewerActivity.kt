package com.kashif.pdfreader

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kashif.pdfreader.ui.theme.PdfReaderTheme
import com.kashif.pdfreader.data.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfReaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Box(modifier = Modifier.padding(padding))
                    PdfViewerApp()
                }
            }
        }
    }
}
@Composable
fun PdfViewerApp() {

    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val openPdfLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedUri = uri
            }
        }
    val repository = remember { PdfRepository(context) }

    LaunchedEffect(selectedUri) {
        selectedUri?.let { uri ->
            withContext(Dispatchers.IO) {
                val renderer = repository.openRenderer(uri)
                val page = renderer.openPage(0)

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

                withContext(Dispatchers.Main) {
                    pageBitmap = bitmap
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {
            openPdfLauncher.launch(arrayOf("application/pdf"))
        }) {
            Text("Open PDF")
        }
        pageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedUri?.toString() ?: "No PDF selected",
            fontSize = 12.sp
        )
    }
}
