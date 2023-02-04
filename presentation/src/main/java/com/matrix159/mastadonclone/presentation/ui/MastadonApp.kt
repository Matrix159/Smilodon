package com.matrix159.mastadonclone.presentation.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.matrix159.mastadonclone.presentation.ui.theme.MastadonTheme
import com.matrix159.mastadonclone.shared.viewmodel.DKMPViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.matrix159.mastadonclone.presentation.ui.components.Router
import com.matrix159.mastadonclone.presentation.ui.components.ScreenPicker
import timber.log.Timber

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MastadonApp(model: DKMPViewModel) {
  val appState by model.stateFlow.collectAsStateWithLifecycle()

  Timber.d("D-KMP-SAMPLE: recomposition Index: " + appState.recompositionIndex.toString())
  MastadonTheme {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding(), // Handle safe drawing insets at the root level
      color = MaterialTheme.colorScheme.surface
    ) {
      Router(model)
    }
  }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewMastadonApp() {
  //MastadonApp(DKMPViewModel(Repository()))
}