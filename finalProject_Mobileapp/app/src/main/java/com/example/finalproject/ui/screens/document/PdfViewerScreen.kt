package com.example.finalproject.ui.screens.document

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.barteksc.pdfviewer.PDFView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val BASE_URL = "http://10.0.2.2:8080"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    fileUrl: String,
    title: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var localFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(fileUrl) {
        scope.launch(Dispatchers.IO) {
            val fullUrl = if (fileUrl.startsWith("http")) fileUrl else "$BASE_URL$fileUrl"
            val file = downloadPdfToCache(context, fullUrl)
            if (file != null && file.exists()) {
                localFile = file
                isLoading = false
            } else {
                isError = true
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, maxLines = 1, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                isLoading -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang tải tài liệu...", color = Color.Gray)
                    }
                }
                isError -> {
                    Text("Không thể tải file PDF", modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
                localFile != null -> {
                    // AndroidView cần xác định kiểu View cụ thể (PDFView)
                    // Nếu import com.github.barteksc.pdfviewer.PDFView thành công, lỗi type T sẽ hết
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PDFView(ctx, null)
                        },
                        update = { pdfView ->
                            pdfView.fromFile(localFile)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .spacing(10)
                                .load()
                        }
                    )
                }
            }
        }
    }
}

suspend fun downloadPdfToCache(context: Context, urlString: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

            val inputStream = connection.inputStream
            val fileName = "temp_pdf_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}