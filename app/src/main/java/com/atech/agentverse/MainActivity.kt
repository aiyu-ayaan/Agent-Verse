package com.atech.agentverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.atech.agentverse.presentation.MainViewModel
import com.atech.agentverse.ui.AgentVerseScreen
import com.atech.agentverse.ui.theme.AgentVerseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AgentVerseTheme {
                val state by viewModel.uiState.collectAsState()

                AgentVerseScreen(
                    state = state,
                    onProviderSelected = viewModel::onProviderSelected,
                    onModelIdChanged = viewModel::onModelIdChanged,
                    onPromptChanged = viewModel::onPromptChanged,
                    onApiKeyChanged = viewModel::onApiKeyChanged,
                    onBaseUrlChanged = viewModel::onBaseUrlChanged,
                    onAppNameChanged = viewModel::onAppNameChanged,
                    onAppRefererChanged = viewModel::onAppRefererChanged,
                    onSaveProviderConfig = viewModel::saveProviderConfig,
                    onSendPrompt = viewModel::sendPrompt,
                )
            }
        }
    }
}