package com.rfm.quran.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rfm.quran.data.AudioPlayer
import com.rfm.quran.data.Reciters
import com.rfm.quran.data.SurahData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioScreen(onBack: () -> Unit) {
    var reciter by remember { mutableStateOf(0) }
    var currentUrl by remember { mutableStateOf(AudioPlayer.currentUrl) }
    var loadingUrl by remember { mutableStateOf<String?>(null) }
    var paused by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الاستماع للقرآن") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        AudioPlayer.stop()
                        currentUrl = null
                        loadingUrl = null
                        paused = false
                    }) { Icon(Icons.Filled.Stop, contentDescription = "إيقاف") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Reciters.list.forEachIndexed { i, r ->
                    FilterChip(
                        selected = reciter == i,
                        onClick = {
                            AudioPlayer.stop()
                            currentUrl = null
                            loadingUrl = null
                            paused = false
                            reciter = i
                        },
                        label = { Text(r.first) }
                    )
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(SurahData.NAMES) { i, name ->
                    val url = Reciters.url(reciter, i + 1)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${i + 1}.",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.width(34.dp)
                            )
                            Text(
                                "سورة $name",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            when {
                                loadingUrl == url -> CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
                                currentUrl == url && !paused -> IconButton(onClick = {
                                    AudioPlayer.pause(); paused = true
                                }) { Icon(Icons.Filled.Pause, contentDescription = "إيقاف مؤقت", tint = MaterialTheme.colorScheme.primary) }
                                currentUrl == url && paused -> IconButton(onClick = {
                                    AudioPlayer.resume(); paused = false
                                }) { Icon(Icons.Filled.PlayArrow, contentDescription = "استئناف", tint = MaterialTheme.colorScheme.primary) }
                                else -> IconButton(onClick = {
                                    loadingUrl = url
                                    AudioPlayer.play(
                                        url = url,
                                        onPrepared = { currentUrl = url; loadingUrl = null; paused = false },
                                        onError = { currentUrl = null; loadingUrl = null }
                                    )
                                }) { Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل", tint = MaterialTheme.colorScheme.primary) }
                            }
                        }
                    }
                }
            }
        }
    }
}
