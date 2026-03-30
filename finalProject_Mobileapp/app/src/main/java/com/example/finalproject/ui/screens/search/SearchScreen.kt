package com.example.finalproject.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.finalproject.R
import com.example.finalproject.data.remote.model.SearchResponse
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

val PrimaryBlue = Color(0xFF2B58C4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasFetched by viewModel.hasFetched.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val currentGrade by viewModel.currentGrade.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(100) // Delay slight to let UI render before showing keyboard
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray, fontSize = 16.sp) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.saveKeyword(query)
                                focusManager.clearFocus()
                            }
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (query.isBlank() && !hasFetched) {
                // Hiển thị lịch sử tìm kiếm
                if (searchHistory.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.search_recent_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Text(stringResource(R.string.search_clear_history), color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(searchHistory) { historyItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.executeSearch(historyItem)
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = historyItem, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            } else {
                // Hiển thị kết quả tìm kiếm
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (results.isEmpty() && hasFetched) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                stringResource(R.string.search_no_results, query),
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(results) { item ->
                            SearchResultCard(item = item, onClick = {
                                viewModel.saveKeyword(query)
                                // Điều hướng tương ứng
                                when(item.type) {
                                    "LESSON" -> navController.navigate(AppRoutes.createLessonDetailRoute(item.id))
                                    "QUIZ" -> navController.navigate(AppRoutes.createQuizFlowRoute(item.subjectCode, currentGrade))
                                    "DOCUMENT" -> navController.navigate(AppRoutes.createDocumentListRoute(item.subjectCode, currentGrade))
                                    else -> {} 
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(item: SearchResponse, onClick: () -> Unit) {
    val icon = when (item.type) {
        "LESSON" -> Icons.Rounded.PlayCircle
        "QUIZ" -> Icons.Rounded.Quiz
        "DOCUMENT" -> Icons.Rounded.Article
        else -> Icons.Rounded.Article
    }
    
    val typeText = when (item.type) {
        "LESSON" -> stringResource(R.string.search_type_lesson)
        "QUIZ" -> stringResource(R.string.search_type_quiz)
        "DOCUMENT" -> stringResource(R.string.search_type_document)
        else -> ""
    }

    val boxColor = when (item.type) {
        "LESSON" -> Color(0xFFE3F2FD) // Light Blue
        "QUIZ" -> Color(0xFFFFF3E0)   // Light Orange
        "DOCUMENT" -> Color(0xFFE8F5E9) // Light Green
        else -> Color.LightGray
    }

    val iconColor = when (item.type) {
        "LESSON" -> PrimaryBlue
        "QUIZ" -> Color(0xFFFF6D00)
        "DOCUMENT" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(boxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titleVi,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = typeText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Môn: ${item.subjectCode.uppercase()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
