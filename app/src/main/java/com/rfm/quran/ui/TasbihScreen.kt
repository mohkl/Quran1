package com.rfm.quran.ui

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DHIKR = listOf("سبحان الله", "الحمد لله", "الله أكبر", "لا إله إلا الله", "أستغفر الله")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbihScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("tasbih", Context.MODE_PRIVATE) }
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }

    var dhikr by remember { mutableStateOf(0) }
    var target by remember { mutableStateOf(33) }
    var count by remember { mutableIntStateOf(prefs.getInt("count_0", 0)) }
    var total by remember { mutableIntStateOf(prefs.getInt("total", 0)) }

    fun vibrate(ms: Long) {
        vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun save() {
        prefs.edit()
            .putInt("count_$dhikr", count)
            .putInt("total", total)
            .apply()
    }

    fun onTap() {
        count += 1
        total += 1
        if (count >= target) {
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 120, 150, 120), -1))
            count = 0
        } else {
            vibrate(25)
        }
        save()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المسبحة الإلكترونية") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        count = 0; save()
                    }) { Icon(Icons.Filled.Refresh, contentDescription = "تصفير") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DHIKR.forEachIndexed { i, d ->
                    FilterChip(selected = dhikr == i, onClick = {
                        vibrate(20)
                        dhikr = i
                        count = prefs.getInt("count_$i", 0)
                    }, label = { Text(d) })
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(33, 100, 1000).forEach { t ->
                    FilterChip(selected = target == t, onClick = { target = t }, label = { Text("$t") })
                }
            }

            LinearProgressIndicator(
                progress = { count / target.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(MaterialTheme.shapes.small)
            )

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onTap() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(DHIKR[dhikr], style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
                    Text(
                        "$count",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("اضغط للتسبيح", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("الهدف: $target — المتبقي: ${target - count}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("مجموع التسبيحات الكلي: $total", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)

            Button(onClick = { count = 0; save() }) { Text("تصفير العداد") }
        }
    }
}
