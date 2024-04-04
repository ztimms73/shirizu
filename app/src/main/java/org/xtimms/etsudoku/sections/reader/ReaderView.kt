package org.xtimms.etsudoku.sections.reader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.request.ImageRequest
import com.google.android.material.slider.Slider
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import org.koitharu.kotatsu.parsers.model.Manga
import org.koitharu.kotatsu.parsers.model.MangaChapter
import org.xtimms.etsudoku.core.components.AppBarTitle
import org.xtimms.etsudoku.core.components.BackIconButton

const val READER_DESTINATION = "reader"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderView(
    readerViewModel: ReaderViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {

    var sliderPosition by remember { mutableStateOf(0f) }
    val pagerState = rememberPagerState { sliderPosition.toInt() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { AppBarTitle(title = "Test", subtitle = "Test") },
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
                navigationIcon = {
                    BackIconButton(onClick = navigateBack)
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                Slider(
                    value = sliderPosition,
                    valueRange = 0f..3f,
                    steps = 3,
                    onValueChange = { sliderPosition = it }
                )
            }
        }
    ) { padding ->
        HorizontalPager(
            modifier = Modifier.padding(padding),
            state = pagerState
        ) {
            ZoomableAsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.unsplash.com/photo-1678465952838-c9d7f5daaa65")
                    .crossfade(1_000)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }
    }
}