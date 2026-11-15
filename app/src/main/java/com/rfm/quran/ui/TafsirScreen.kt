package com.rfm.quran.ui

import android.text.Html
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rfm.quran.data.QuranApi
import com.rfm.quran.data.SurahData
import com.rfm.quran.data.TafsirSources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TafsirScreen(onBack: () -> Unit) {
    var surah by remember { mutableStateOf(1) }
    var tafsirId by remember { mutableStateOf(TafsirSources.list.first().first) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun load() {
        loading = true
        error = null
        scope.launch(Dispatchers.IO) {
            try {
                val resp = QuranApi.service.tafsir(tafsirId, surah)
                val sb = StringBuilder()
                resp.tafsirs?.forEach { a ->
                    sb.append(Html.fromHtml(a.text ?: "", Html.FROM_HTML_MODE_LEGACY)).append("\n\n")
                }
                withContext(Dispatchers.Main) {
                    text = sb.toString().trim()
                    loading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = "تعذر تحميل التفسير، تحقق من الاتصال بالإنترنت"
                    loading = false
                }
            }
        }
    }

    LaunchedEffect(surah, tafsirId) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التفسير الكامل") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                var surahExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = surahExpanded,
                    onExpandedChange = { surahExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "سورة ${SurahData.NAMES[surah - 1]}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("السورة") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(surahExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = surahExpanded, onDismissRequest = { surahExpanded = false }) {
                        SurahData.NAMES.forEachIndexed { i, name ->
                            DropdownMenuItem(
                                text = { Text("${i + 1}. $name") },
                                onClick = { surah = i + 1; surahExpanded = false }
                            )
                        }
                    }
                }

                var tafsirExpanded by remember { mutableStateOf(false) }
                val tafsirName = TafsirSources.list.first { it.first == tafsirId }.second
                ExposedDropdownMenuBox(
                    expanded = tafsirExpanded,
                    onExpandedChange = { tafsirExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = tafsirName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("التفسير") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tafsirExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = tafsirExpanded, onDismissRequest = { tafsirExpanded = false }) {
                        TafsirSources.list.forEach { (id, name) ->
                            DropdownMenuItem(text = { Text(name) }, onClick = { tafsirId = id; tafsirExpanded = false })
                        }
                    }
                }
            }

            when {
                loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { load() }) { Text("إعادة المحاولة") }
                    }
                }
                else -> Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "تفسير سورة ${SurahData.NAMES[surah - 1]} — $tafsirName",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}
