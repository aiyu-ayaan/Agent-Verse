package com.atech.agentverse.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atech.agentverse.presentation.MainUiState
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.ui_common.components.AvChatBubble
import com.atech.ui_common.components.AvLabeledField
import com.atech.ui_common.components.AvProviderSwitcher
import com.atech.ui_common.components.AvSectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentVerseScreen(
    state: MainUiState,
    onProviderSelected: (AvProvider) -> Unit,
    onModelIdChanged: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onBaseUrlChanged: (String) -> Unit,
    onAppNameChanged: (String) -> Unit,
    onAppRefererChanged: (String) -> Unit,
    onSaveProviderConfig: () -> Unit,
    onSendPrompt: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AgentVerse") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AvSectionCard(title = "Provider Setup") {
                AvProviderSwitcher(
                    providers = AvProvider.entries.map { it.name },
                    selectedProvider = state.selectedProvider.name,
                    onProviderSelected = { onProviderSelected(AvProvider.fromValue(it)) },
                )
                AvLabeledField(
                    label = "Model ID",
                    value = state.modelId,
                    onValueChange = onModelIdChanged,
                    placeholder = "e.g. llama-3.3-70b-versatile",
                )
                AvLabeledField(
                    label = "API Key",
                    value = state.apiKey,
                    onValueChange = onApiKeyChanged,
                    isSecret = true,
                )
                AvLabeledField(
                    label = "Base URL (optional)",
                    value = state.baseUrl,
                    onValueChange = onBaseUrlChanged,
                )
                AvLabeledField(
                    label = "App Name (OpenRouter optional)",
                    value = state.appName,
                    onValueChange = onAppNameChanged,
                )
                AvLabeledField(
                    label = "Referer (OpenRouter optional)",
                    value = state.appReferer,
                    onValueChange = onAppRefererChanged,
                    placeholder = "https://yourdomain.com",
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onSaveProviderConfig) {
                        Text("Save Provider")
                    }
                }
            }

            AvSectionCard(title = "Chat") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (state.messages.isEmpty()) {
                        Text(
                            text = "No messages yet. Start chatting.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        state.messages.forEach { message ->
                            AvChatBubble(
                                roleLabel = "${message.role} • ${message.modelId}",
                                content = message.content,
                                isUser = message.role == AvRole.USER.name,
                            )
                        }
                    }
                }
                AvLabeledField(
                    label = "Prompt",
                    value = state.prompt,
                    onValueChange = onPromptChanged,
                    singleLine = false,
                    placeholder = "Ask something...",
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onSendPrompt,
                        enabled = !state.isSending,
                    ) {
                        Text(if (state.isSending) "Sending..." else "Send")
                    }
                }
            }

            AvSectionCard(title = "Token Usage") {
                if (state.providerUsage.isEmpty()) {
                    Text("No token usage yet.")
                } else {
                    state.providerUsage.forEach { usage ->
                        Text(
                            text = "${usage.provider.name}: ${usage.totalTokens} tokens (${usage.totalRequests} requests)",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            state.errorMessage?.takeIf { it.isNotBlank() }?.let { errorText ->
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
