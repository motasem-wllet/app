package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun DeveloperToolbar(
    currentUser: UserProfile,
    allUsers: List<UserProfile>,
    onSwitchUser: (String) -> Unit
) {
    Surface(
        color = ObsidianSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(NeonEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محاكي المطور الحي (Live Sandbox)",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allUsers.forEach { user ->
                    val isSelected = user.id == currentUser.id
                    Surface(
                        onClick = { onSwitchUser(user.id) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NeonEmerald.copy(alpha = 0.2f) else ObsidianCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonEmerald else ObsidianBorder
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (user.role == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isSelected) NeonEmerald else TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = user.name.split(" ")[0] + " (${if (user.role == "admin") "مدير" else "مستخدم"})",
                                color = if (isSelected) NeonEmerald else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
