package com.rfm.quran.ui

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.rfm.quran.data.SurahData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("quran_reader", Context.MODE_PRIVATE) }
    val startPage = remember { prefs.getInt("last_page", 0).coerceIn(0, 603) }
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { 604 })
    var showJump by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        prefs.edit().putInt("last_page", pagerState.currentPage).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المصحف — صفحة ${pagerState.currentPage + 1} / 604") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { showJump = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "انتقال")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = SurahData.pageUrl(page + 1),
                    contentDescription = "صفحة ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    placeholder = ColorPainter(Color(0xFF21262D)),
                    error = ColorPainter(Color(0xFF21262D))
                )
            }
        }

        if (showJump) {
            var input by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showJump = false },
                title = { Text("الانتقال إلى صفحة") },
                text = {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it.filter { ch -> ch.isDigit() } },
                        label = { Text("رقم الصفحة (1 - 604)") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val n = input.toIntOrNull()
                        if (n != null && n in 1..604) {
                            scope.launch { pagerState.scrollToPage(n - 1) }
                            showJump = false
                        }
                    }) { Text("انتقال") }
                },
                dismissButton = {
                    Button(onClick = { showJump = false }) { Text("إلغاء") }
                }
            )
        }
    }
}
