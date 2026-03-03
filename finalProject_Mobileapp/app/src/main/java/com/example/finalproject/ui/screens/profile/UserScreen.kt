package com.example.finalproject.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finalproject.ui.viewmodel.UserViewModel

// Định nghĩa mã màu Light Theme
val LightBackground = Color(0xFFFAFAFA)
val CardBackground = Color.White
val PrimaryBlue = Color(0xFF1890FF)
val TextPrimary = Color(0xFF333333)
val TextSecondary = Color.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserViewModel = hiltViewModel()
) {
    val profile by viewModel.profile
    val isLoading by viewModel.isLoading
    var showEditDialog by remember { mutableStateOf(false) }

    if (isLoading && profile == null) {
        Box(modifier = Modifier.fillMaxSize().background(LightBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    Scaffold(
        containerColor = LightBackground,
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ người dùng", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = { /* Mở Cài đặt hệ thống (Đăng xuất, Đổi mật khẩu...) */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ================== AVATAR & INFO ==================
            item {
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(top = 16.dp)) {
                    AsyncImage(
                        model = profile?.avatarUrl ?: "https://api.dicebear.com/7.x/avataaars/png?seed=${profile?.username}",
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, PrimaryBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { showEditDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profile?.username ?: "Chưa cập nhật",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Lớp 8A2", // Chỗ này có thể mock hoặc nối từ ViewModel sau
                        color = PrimaryBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFFE6F7FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("• THCS Cầu Giấy", color = TextSecondary, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ================== STATS MOCK ==================
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatCard("42", "Bài học", Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    StatCard("9.2", "Điểm TB", Modifier.weight(1f), Color(0xFF52C41A)) // Xanh lá
                    Spacer(modifier = Modifier.width(12.dp))
                    StatCard("128h", "Giờ học", Modifier.weight(1f), Color(0xFFFAAD14)) // Vàng cam
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // ================== THÔNG TIN CÁ NHÂN ==================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Thông tin cá nhân", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(Icons.Default.Email, "Email", profile?.email ?: "Chưa cập nhật")
                    InfoRow(Icons.Default.Phone, "Số điện thoại", "0912 *** ***") // Chưa có trong DB
                    InfoRow(Icons.Default.DateRange, "Ngày sinh", profile?.dateOfBirth ?: "Chưa cập nhật")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // ================== DIALOG CHỈNH SỬA ==================
        if (showEditDialog) {
            EditProfileDialog(
                currentAvatar = profile?.avatarUrl ?: "",
                currentDob = profile?.dateOfBirth ?: "",
                onDismiss = { showEditDialog = false },
                onSave = { newAvatar, newDob ->
                    viewModel.updateProfile(
                        avatarUrl = newAvatar.ifBlank { null },
                        dateOfBirth = newDob.ifBlank { null }
                    )
                    showEditDialog = false
                }
            )
        }
    }
}

// ================== COMPONENT CON DÀNH CHO LIGHT THEME ==================

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier, valueColor: Color = PrimaryBlue) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 13.sp, color = TextSecondary)
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon bọc trong một hình tròn màu xanh nhạt
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE6F7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentAvatar: String,
    currentDob: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var avatarUrl by remember { mutableStateOf(currentAvatar) }
    var dob by remember { mutableStateOf(currentDob) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Link ảnh Avatar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Ngày sinh (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(avatarUrl, dob) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Lưu thay đổi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = TextSecondary)
            }
        }
    )
}