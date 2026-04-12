package com.example.finalproject.ui.screens.document

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

const val BASE_URL = "http://192.168.0.159:8080"

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

    DisposableEffect(Unit) {
        onDispose {
            localFile?.let {
                if (it.exists()) it.delete()
            }
        }
    }

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
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.pdf_viewer_loading), color = Color.Gray)
                    }
                }
                isError -> {
                    Text(
                        stringResource(id = R.string.pdf_viewer_error_load),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                localFile != null -> {
                    PdfNativeViewer(file = localFile!!)
                }
            }
        }
    }
}

@Composable
fun PdfNativeViewer(file: File) {
    var pdfRenderer by remember { mutableStateOf<PdfRenderer?>(null) }
    var fileDescriptor by remember { mutableStateOf<ParcelFileDescriptor?>(null) }
    val pdfMutex = remember { Mutex() }

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    DisposableEffect(file) {
        try {
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fd)
            pdfRenderer = renderer
            fileDescriptor = fd
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            pdfRenderer?.close()
            fileDescriptor?.close()
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }

    pdfRenderer?.let { renderer ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        val maxOffsetX = (size.width * (scale - 1)) / 2
                        val maxOffsetY = (size.height * (scale - 1)) / 2
                        offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                    }
                }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(renderer.pageCount) { index ->
                    PdfPageItem(
                        renderer = renderer,
                        pageIndex = index,
                        mutex = pdfMutex,
                        screenWidth = screenWidthPx
                    )
                }
            }
        }
    }
}

@Composable
fun PdfPageItem(
    renderer: PdfRenderer,
    pageIndex: Int,
    mutex: Mutex,
    screenWidth: Int
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    DisposableEffect(pageIndex) {
        onDispose {
            bitmap = null
        }
    }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    val page = renderer.openPage(pageIndex)
                    val qualityFactor = 1.5f
                    val scale = (screenWidth.toFloat() * qualityFactor) / page.width
                    val width = (page.width * scale).toInt()
                    val height = (page.height * scale).toInt()

                    val renderBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    renderBitmap.eraseColor(android.graphics.Color.WHITE)

                    page.render(renderBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()

                    bitmap = renderBitmap
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Lấy chuỗi mô tả số trang
    val pageDesc = stringResource(id = R.string.pdf_viewer_page_desc, pageIndex + 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = pageDesc,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Gray, modifier = Modifier.size(24.dp))
            }
        }
    }
}

suspend fun downloadPdfToCache(context: Context, urlString: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            Log.d("PdfViewer", "Đang tải PDF từ URL: $urlString")

            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            val responseCode = connection.responseCode
            Log.d("PdfViewer", "Mã phản hồi từ Server: $responseCode")

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("PdfViewer", "Lỗi mạng hoặc bị Server chặn! Response: ${connection.responseMessage}")
                return@withContext null
            }

            val inputStream = connection.inputStream
            val fileName = "temp_pdf_${urlString.hashCode()}.pdf"
            val file = File(context.cacheDir, fileName)

            if (file.exists()) file.delete()
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Log.d("PdfViewer", "Tải file thành công, dung lượng: ${file.length()} bytes")
            return@withContext file

        } catch (e: Exception) {
            Log.e("PdfViewer", "Lỗi Exception khi tải file: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }
}