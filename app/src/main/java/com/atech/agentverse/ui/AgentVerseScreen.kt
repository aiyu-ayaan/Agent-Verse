package com.atech.agentverse.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atech.agentverse.presentation.MainUiState
import com.atech.agentverse.presentation.ScreenDestination
import com.atech.agentverse.presentation.SettingsTab
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.core.model.ProviderUsageSummary
import com.atech.ui_common.components.AvChatBubble
import com.atech.ui_common.components.AvLabeledField
import com.atech.ui_common.components.AvProviderSwitcher
import com.atech.ui_common.components.AvSectionCard
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentVerseScreen(
    state: MainUiState,
    onOpenChatScreen: () -> Unit,
    onOpenSettingsScreen: () -> Unit,
    onSettingsTabSelected: (SettingsTab) -> Unit,
    onCreateNewChat: () -> Unit,
    onConversationSelected: (String) -> Unit,
    onProviderSelected: (AvProvider) -> Unit,
    onModelIdChanged: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onBaseUrlChanged: (String) -> Unit,
    onAppNameChanged: (String) -> Unit,
    onAppRefererChanged: (String) -> Unit,
    onSaveProviderConfig: () -> Unit,
    onMemorySizeChanged: (Int) -> Unit,
    onStreamOutputChanged: (Boolean) -> Unit,
    onResetTokenUsage: () -> Unit,
    onSendPrompt: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(320.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "AgentVerse",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Button(
                        onClick = {
                            onCreateNewChat()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "+ New Chat")
                    }
                    NavigationDrawerItem(
                        label = { Text("Chat") },
                        selected = state.activeScreen == ScreenDestination.CHAT,
                        onClick = {
                            onOpenChatScreen()
                            scope.launch { drawerState.close() }
                        },
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = state.activeScreen == ScreenDestination.SETTINGS,
                        onClick = {
                            onOpenSettingsScreen()
                            scope.launch { drawerState.close() }
                        },
                    )
                    HorizontalDivider()
                    Text(
                        text = "Chat History",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(state.conversations, key = { it.conversationId }) { conversation ->
                            NavigationDrawerItem(
                                label = {
                                    Column {
                                        Text(conversation.title, maxLines = 1)
                                        Text(
                                            text = formatDate(conversation.updatedAtEpochMs),
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }
                                },
                                selected = state.selectedConversationId == conversation.conversationId,
                                onClick = {
                                    onConversationSelected(conversation.conversationId)
                                    scope.launch { drawerState.close() }
                                },
                            )
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (state.activeScreen == ScreenDestination.CHAT) {
                                state.conversations
                                    .firstOrNull { it.conversationId == state.selectedConversationId }
                                    ?.title
                                    ?: "New Chat"
                            } else {
                                "Settings"
                            },
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Text("Menu")
                        }
                    },
                    actions = {
                        if (state.activeScreen == ScreenDestination.CHAT) {
                            IconButton(onClick = onOpenSettingsScreen) {
                                Text("Settings")
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            when (state.activeScreen) {
                ScreenDestination.CHAT -> ChatScreenContent(
                    state = state,
                    onPromptChanged = onPromptChanged,
                    onSendPrompt = onSendPrompt,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )

                ScreenDestination.SETTINGS -> SettingsScreenContent(
                    state = state,
                    onSettingsTabSelected = onSettingsTabSelected,
                    onProviderSelected = onProviderSelected,
                    onModelIdChanged = onModelIdChanged,
                    onApiKeyChanged = onApiKeyChanged,
                    onBaseUrlChanged = onBaseUrlChanged,
                    onAppNameChanged = onAppNameChanged,
                    onAppRefererChanged = onAppRefererChanged,
                    onSaveProviderConfig = onSaveProviderConfig,
                    onMemorySizeChanged = onMemorySizeChanged,
                    onStreamOutputChanged = onStreamOutputChanged,
                    onResetTokenUsage = onResetTokenUsage,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun ChatScreenContent(
    state: MainUiState,
    onPromptChanged: (String) -> Unit,
    onSendPrompt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (state.messages.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "How can I help you today?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Start a conversation from the input below.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        AvChatBubble(
                            roleLabel = "${message.role} • ${message.modelId}",
                            content = message.content,
                            isUser = message.role == AvRole.USER.name,
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.prompt,
            onValueChange = onPromptChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Message AgentVerse") },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(onClick = onSendPrompt, enabled = !state.isSending) {
                Text(if (state.isSending) "Sending..." else "Send")
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

@Composable
private fun SettingsScreenContent(
    state: MainUiState,
    onSettingsTabSelected: (SettingsTab) -> Unit,
    onProviderSelected: (AvProvider) -> Unit,
    onModelIdChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onBaseUrlChanged: (String) -> Unit,
    onAppNameChanged: (String) -> Unit,
    onAppRefererChanged: (String) -> Unit,
    onSaveProviderConfig: () -> Unit,
    onMemorySizeChanged: (Int) -> Unit,
    onStreamOutputChanged: (Boolean) -> Unit,
    onResetTokenUsage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = SettingsTab.entries

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PrimaryTabRow(selectedTabIndex = tabs.indexOf(state.settingsTab)) {
            tabs.forEach { tab ->
                Tab(
                    selected = state.settingsTab == tab,
                    onClick = { onSettingsTabSelected(tab) },
                    text = { Text(tab.name.lowercase().replaceFirstChar { it.titlecase() }) },
                )
            }
        }

        when (state.settingsTab) {
            SettingsTab.INTEGRATION -> IntegrationSettings(
                state = state,
                onProviderSelected = onProviderSelected,
                onModelIdChanged = onModelIdChanged,
                onApiKeyChanged = onApiKeyChanged,
                onBaseUrlChanged = onBaseUrlChanged,
                onAppNameChanged = onAppNameChanged,
                onAppRefererChanged = onAppRefererChanged,
                onSaveProviderConfig = onSaveProviderConfig,
            )

            SettingsTab.TOKENS -> TokenSettings(
                usage = state.providerUsage,
                onResetTokenUsage = onResetTokenUsage,
            )

            SettingsTab.CHATS -> ChatSettings(
                memorySize = state.chatSettings.memorySize,
                streamOutput = state.chatSettings.streamOutput,
                onMemorySizeChanged = onMemorySizeChanged,
                onStreamOutputChanged = onStreamOutputChanged,
            )
        }
    }
}

@Composable
private fun IntegrationSettings(
    state: MainUiState,
    onProviderSelected: (AvProvider) -> Unit,
    onModelIdChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onBaseUrlChanged: (String) -> Unit,
    onAppNameChanged: (String) -> Unit,
    onAppRefererChanged: (String) -> Unit,
    onSaveProviderConfig: () -> Unit,
) {
    AvSectionCard(title = "Integrations") {
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
                Text("Save Integration")
            }
        }
    }
}

@Composable
private fun TokenSettings(
    usage: List<ProviderUsageSummary>,
    onResetTokenUsage: () -> Unit,
) {
    AvSectionCard(title = "Token Usage") {
        if (usage.isEmpty()) {
            Text("No token usage recorded yet.")
        } else {
            usage.forEach { row ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = row.provider.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text("Total: ${row.totalTokens} tokens")
                    Text("Prompt: ${row.totalPromptTokens} • Completion: ${row.totalCompletionTokens}")
                    Text("Requests: ${row.totalRequests}")
                    HorizontalDivider()
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onResetTokenUsage) {
                Text("Reset Token Stats")
            }
        }
    }
}

@Composable
private fun ChatSettings(
    memorySize: Int,
    streamOutput: Boolean,
    onMemorySizeChanged: (Int) -> Unit,
    onStreamOutputChanged: (Boolean) -> Unit,
) {
    AvSectionCard(title = "Chat Settings") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Conversation memory window: $memorySize messages")
            Slider(
                value = memorySize.toFloat(),
                onValueChange = { onMemorySizeChanged(it.toInt().coerceIn(2, 50)) },
                valueRange = 2f..50f,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Stream output")
                Switch(
                    checked = streamOutput,
                    onCheckedChange = onStreamOutputChanged,
                )
            }
            Text(
                text = "Streaming requires provider support. If disabled, responses return in one chunk.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun formatDate(epochMs: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(epochMs))
