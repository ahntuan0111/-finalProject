package com.example.finalproject.ui.screens.document

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.DocumentDto
import com.example.finalproject.ui.components.AppNotificationManager
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.viewmodel.DocumentViewModel
import com.example.finalproject.utils.AppLogger
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    navController: NavController,
    subjectCode: String,
    grade: Int,
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(subjectCode, grade) {
        viewModel.fetchDocuments(subjectCode, grade)
    }



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.doc_list_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.common_back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryBlue)
            } else if (documents.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.doc_list_empty),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(documents) { doc ->
                        DocumentItemCard(doc) {
                            val safeUrl = doc.filePath ?: ""
                            val safeTitle = doc.getDisplayTitle()

                            if (safeUrl.isEmpty()) {
                                AppLogger.w("DocumentList", "Doc URL empty for: $safeTitle")
                                AppNotificationManager.error("⚠️ Tài liệu này chưa có file, vui lòng thử lại sau")
                                return@DocumentItemCard
                            }

                            try {
                                val urlBytes = safeUrl.toByteArray(StandardCharsets.UTF_8)
                                val base64Url = android.util.Base64.encodeToString(urlBytes, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
                                
                                val titleBytes = safeTitle.toByteArray(StandardCharsets.UTF_8)
                                val base64Title = android.util.Base64.encodeToString(titleBytes, android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
                                
                                navController.navigate("pdf_viewer/$base64Url/$base64Title")
                            } catch (e: Exception) {
                                AppLogger.e("DocumentList", "Encode URL failed", e)
                                AppNotificationManager.error("⚠️ Không thể mở tài liệu, vui lòng thử lại")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentItemCard(document: DocumentDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.getDisplayTitle(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val desc = document.getDisplayDescription()
                if (!desc.isNullOrEmpty()) {
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = document.getFormattedSize(),
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        // Chuỗi động: "<size> • <date>"
                        text = stringResource(id = R.string.doc_list_item_meta, "", document.getFormattedDate()),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}