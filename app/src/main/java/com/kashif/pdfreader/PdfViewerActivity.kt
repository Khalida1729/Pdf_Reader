package com.kashif.pdfreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
                Log.d("PDF", "Page count = ${renderer.pageCount}")
                renderer.close()
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = selectedUri?.toString() ?: "No PDF selected",
            fontSize = 12.sp
        )
    }
}
