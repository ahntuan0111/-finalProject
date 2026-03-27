package com.example.finalproject.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finalproject.ui.navigation.AppRoutes
import com.example.finalproject.ui.theme.PrimaryBlue
import com.example.finalproject.ui.theme.SecondaryBlue
import com.example.finalproject.ui.viewmodel.UserViewModel

// --- MODERN PREMIUM COLORS & BRUSHES ---
val PremiumGradient = Brush.verticalGradient(
    colors = listOf(PrimaryBlue, SecondaryBlue)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val profile by viewModel.profile
    val isLoading by viewModel.isLoading
    val currentGrade by viewModel.currentGrade.collectAsState()
    val isDarkThemePref by viewModel.isDarkTheme.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // ĐÃ SỬA LỖI 1: Chuyển khai báo biến lên trước LaunchedEffect
    var showEditSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UserViewModel.UserEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                UserViewModel.UserEvent.UpdateSuccess -> {
                    showEditSheet = false
                }
            }
        }
    }

    if (isLoading && profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ================== IMMERSIVE HEADER ==================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Curved/Gradient Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                            .background(PremiumGradient)
                    )

                    // Top Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                        Text(
                            "Hồ sơ của tôi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = { /* Settings context menu */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                            }
                        }
                    }

                    // Profile Summary Card (Overlapping)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar with Premium Border
                            Box(contentAlignment = Alignment.BottomEnd) {
                                AsyncImage(
                                    model = profile?.avatarUrl ?: "https://api.dicebear.com/7.x/avataaars/png?seed=${profile?.username}",
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(85.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, PrimaryBlue, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue)
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable { showEditSheet = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.username ?: "Người dùng",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = PrimaryBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "Lớp $currentGrade",
                                            color = PrimaryBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ID: #${profile?.username?.take(4)?.uppercase()}", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // ================== STATISTICS GRID ==================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PremiumStatItem("42", "Bài học", Icons.Outlined.AutoStories, Color(0xFF3B82F6), Modifier.weight(1f))
                    PremiumStatItem("9.2", "Điểm TB", Icons.Outlined.Insights, Color(0xFF10B981), Modifier.weight(1f))
                    PremiumStatItem("128h", "Giờ học", Icons.Outlined.Timer, Color(0xFFF59E0B), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ================== PERSONAL INFORMATION ==================
            item {
                SectionLabel("Thông tin cá nhân")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        ProfileInfoRow(Icons.Outlined.Email, "Email", profile?.email ?: "Chưa cập nhật")
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.1f))
                        ProfileInfoRow(Icons.Outlined.Person, "Tên hiển thị", profile?.username ?: "Chưa cập nhật")
                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.1f))
                        ProfileInfoRow(Icons.Outlined.Cake, "Ngày sinh", profile?.dateOfBirth ?: "Chưa cập nhật")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ================== GRADE SELECTION ==================
            item {
                SectionLabel("Khối lớp học tập")
                PremiumGradeGroup(
                    currentGrade = currentGrade,
                    onGradeSelected = { viewModel.saveGrade(it) }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ================== PREFERENCES SECTION ==================
            item {
                SectionLabel("Cài đặt hệ thống")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Language
                        PreferenceRow(Icons.Outlined.Translate, "Ngôn ngữ hiển thị") {
                            PremiumLanguagePicker(
                                currentLang = currentLanguage,
                                onLanguageSelect = { viewModel.changeLanguage(it) }
                            )
                        }

                        Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.1f))

                        // Dark Mode
                        val isDark = isDarkThemePref ?: androidx.compose.foundation.isSystemInDarkTheme()
                        PreferenceRow(if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode, "Chế độ tối cảnh") {
                            Switch(
                                checked = isDark,
                                onCheckedChange = { viewModel.toggleDarkMode(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Logout Action
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { 
                            viewModel.logout()
                            // Navigate to WELCOME and clear backstack
                            navController.navigate(AppRoutes.WELCOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    color = Color.Red.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đăng xuất tài khoản", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // ================== EDIT PROFILE BOTTOM SHEET ==================
        if (showEditSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEditSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                EditProfileSheetContent(
                    initialAvatar = profile?.avatarUrl ?: "",
                    initialDob = profile?.dateOfBirth ?: "",
                    onDismiss = { showEditSheet = false },
                    onSave = { avatar, dob ->
                        viewModel.updateProfile(avatar, dob)
                        showEditSheet = false
                    }
                )
            }
        }
    }
}

// ================== PREMIUM ATOMS & COMPONENTS ==================

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 12.dp)
    )
}

@Composable
fun PremiumStatItem(value: String, label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryBlue.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PreferenceRow(icon: ImageVector, label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
        content()
    }
}

@Composable
fun PremiumLanguagePicker(currentLang: String, onLanguageSelect: (String) -> Unit) {
    Surface(
        color = Color.LightGray.copy(alpha = 0.1f),
        shape = CircleShape,
        modifier = Modifier.padding(4.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf("vi", "en").forEach { lang ->
                val selected = currentLang == lang
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onLanguageSelect(lang) },
                    color = if (selected) PrimaryBlue else Color.Transparent,
                    shape = CircleShape
                ) {
                    Text(
                        text = lang.uppercase(),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = if (selected) Color.White else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumGradeGroup(currentGrade: Int, onGradeSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        (6..9).forEach { grade ->
            val selected = currentGrade == grade
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onGradeSelected(grade) },
                color = if (selected) PrimaryBlue else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = if (!selected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)) else null,
                tonalElevation = if (selected) 4.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Lớp",
                            fontSize = 11.sp,
                            color = if (selected) Color.White.copy(alpha = 0.8f) else Color.Gray
                        )
                        Text(
                            "$grade",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileSheetContent(
    initialAvatar: String,
    initialDob: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var avatar by remember { mutableStateOf(initialAvatar) }
    var dob by remember { mutableStateOf(initialDob) }

    // Thêm Launcher để chọn ảnh từ thư viện
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            avatar = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Thông tin cá nhân", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        Text("Thay đổi ảnh đại diện và ngày sinh của bạn", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Preview
        Surface(
            modifier = Modifier.size(110.dp).clickable { launcher.launch("image/*") },
            shape = CircleShape,
            border = BorderStroke(4.dp, PrimaryBlue),
            tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = avatar.ifBlank { "https://api.dicebear.com/7.x/avataaars/png?seed=preview" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // Biểu tượng máy ảnh nhạt để gợi ý bấm vào
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = "Chọn ảnh",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape).padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Fields
        OutlinedTextField(
            value = avatar,
            onValueChange = { avatar = it },
            label = { Text("Link ảnh Avatar") },
            placeholder = { Text("Chạm vào ảnh trên để chọn từ thư viện") },
            readOnly = true, // Khóa nhập tay để dùng thư viện
            modifier = Modifier.fillMaxWidth().clickable { launcher.launch("image/*") },
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Ngày sinh (YYYY-MM-DD)") },
            placeholder = { Text("Ví dụ: 2010-05-20") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, PrimaryBlue)
            ) {
                Text("Hủy bỏ", color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onSave(avatar, dob) },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Cập nhật", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}