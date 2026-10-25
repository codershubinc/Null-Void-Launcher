package com.codershubinc.nullvoidlauncher.ui.settings.widgets.steps

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.system.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.system.steps.StepsInfoState
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitBlue
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.GoogleFitGreen

@Composable
fun StepsHealthConnectSection(
    liveSteps: StepsInfoState,
    onRequestHealthConnectPermission: () -> Unit,
    onOpenGoogleFitOrHealth: () -> Unit
) {
    val context = LocalContext.current

    // Zero records notice if Health Connect has no records from Google Fit
    if (liveSteps.hasHealthConnectPermission && (liveSteps.healthConnectSteps == null || liveSteps.healthConnectSteps == 0)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFBBC05).copy(alpha = 0.12f))
                .border(1.dp, Color(0xFFFBBC05).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .clickable { onOpenGoogleFitOrHealth() }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = Color(0xFFFBBC05),
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Google Fit has 0 records in Health Connect",
                    color = Color(0xFFFBBC05),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "To enable sync: Open Google Fit > Profile > Settings ⚙️ > Turn ON 'Sync Fit with Health Connect'. Falling back to hardware sensor in the meantime.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }

    // Health Connect integration CTA
    if (liveSteps.isHealthConnectAvailable) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (liveSteps.hasHealthConnectPermission) GoogleFitGreen.copy(alpha = 0.12f)
                    else GoogleFitBlue.copy(alpha = 0.12f)
                )
                .border(
                    1.dp,
                    if (liveSteps.hasHealthConnectPermission) GoogleFitGreen.copy(alpha = 0.3f)
                    else GoogleFitBlue.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp)
                )
                .clickable {
                    if (!liveSteps.hasHealthConnectPermission) {
                        onRequestHealthConnectPermission()
                    } else {
                        onOpenGoogleFitOrHealth()
                    }
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (liveSteps.hasHealthConnectPermission) Icons.Rounded.CheckCircle else Icons.Rounded.FitnessCenter,
                contentDescription = null,
                tint = if (liveSteps.hasHealthConnectPermission) GoogleFitGreen else GoogleFitBlue,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (liveSteps.hasHealthConnectPermission) "Google Fit / Health Connect Synced" else "Sync with Google Fit (Health Connect)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (liveSteps.hasHealthConnectPermission) "Importing official daily steps directly from Google Fit & Health Connect" else "Tap Connect to link. Make sure 'Sync Fit with Health Connect' is on in Google Fit settings.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
            if (!liveSteps.hasHealthConnectPermission) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoogleFitBlue)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Connect",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }

    // Direct Google Fit App Widget Section
    ModernCard {
        val fitWidgets = remember { StepsHelper.getGoogleFitWidgets(context) }
        val isFitInstalled = remember { StepsHelper.isGoogleFitInstalled(context) }
        var pinMessage by remember { mutableStateOf<String?>(null) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoogleFitBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = GoogleFitBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add Direct Google Fit App Widget",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add official widgets provided by the Google Fit application",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isFitInstalled) {
                Text(
                    text = "Google Fit is not installed on this device. Install Google Fit to sync step telemetry.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoogleFitBlue)
                        .clickable { onOpenGoogleFitOrHealth() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Get Google Fit",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoogleFitBlue.copy(alpha = 0.12f))
                        .border(1.dp, GoogleFitBlue.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Native Google Fit Integration Active",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "NullVoid renders Google Fit data directly on your home screen via the native Steps Widget above. Android minimalist launchers do not host traditional OS app widget panels.",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoogleFitBlue)
                                .clickable { onOpenGoogleFitOrHealth() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Open Google Fit App",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (fitWidgets.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .clickable {
                                        val success = StepsHelper.requestPinGoogleFitWidget(context, fitWidgets.firstOrNull())
                                        pinMessage = if (success) "Widget pin requested!" else "Launcher widget pin not supported by current system"
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Request OS Pin",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            if (pinMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pinMessage ?: "",
                    color = GoogleFitGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
