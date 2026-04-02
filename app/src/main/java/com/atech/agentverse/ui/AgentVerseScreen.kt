package com.atech.agentverse.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlin.math.roundToInt

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

    val pageBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.background,
        ),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(330.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                DrawerContent(
                    state = state,
                    onCreateNewChat = {
                        onCreateNewChat()
                        scope.launch { drawerState.close() }
                    },
                    onOpenChatScreen = {
                        onOpenChatScreen()
                        scope.launch { drawerState.close() }
                    },
                    onOpenSettingsScreen = {
                        onOpenSettingsScreen()
                        scope.launch { drawerState.close() }
                    },
                    onConversationSelected = {
                        onConversationSelected(it)
                        scope.launch { drawerState.close() }
                    },
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground),
        ) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (state.activeScreen == ScreenDestination.CHAT) {
                                    state.conversations
                                        .firstOrNull { it.conversationId == state.selectedConversationId }
                                        ?.title
                                        ?: "New Chat"
                                } else {
                                    "Settings"
                                },
                                maxLines = 1,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            if (state.activeScreen == ScreenDestination.CHAT) {
                                IconButton(onClick = onOpenSettingsScreen) {
                                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
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
}

@Composable
private fun DrawerContent(
    state: MainUiState,
    onCreateNewChat: () -> Unit,
    onOpenChatScreen: () -> Unit,
    onOpenSettingsScreen: () -> Unit,
    onConversationSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "AgentVerse",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Built for multi-model workflows",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onCreateNewChat,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Text(text = "  New Chat")
                }
            }
        }

        NavigationDrawerItem(
            label = { Text("Chat") },
            selected = state.activeScreen == ScreenDestination.CHAT,
            onClick = onOpenChatScreen,
            shape = RoundedCornerShape(14.dp),
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = state.activeScreen == ScreenDestination.SETTINGS,
            onClick = onOpenSettingsScreen,
            shape = RoundedCornerShape(14.dp),
        )

        HorizontalDivider()

        Text(
            text = "History",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.conversations, key = { it.conversationId }) { conversation ->
                val selected = state.selectedConversationId == conversation.conversationId
                Surface(
                    onClick = { onConversationSelected(conversation.conversationId) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    tonalElevation = if (selected) 3.dp else 0.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = conversation.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Text(
                            text = formatDate(conversation.updatedAtEpochMs),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
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
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            tonalElevation = 1.dp,
        ) {
            if (state.messages.isEmpty()) {
                EmptyConversationView(modifier = Modifier.fillMaxSize())
            } else {
                MessageTimeline(state = state, listState = listState)
            }
        }

        ComposerBar(
            prompt = state.prompt,
            isSending = state.isSending,
            onPromptChanged = onPromptChanged,
            onSendPrompt = onSendPrompt,
            errorMessage = state.errorMessage,
        )
    }
}

@Composable
private fun EmptyConversationView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Start a new conversation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Ask anything, write code, summarize docs, or switch providers from settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SuggestionChip(icon = Icons.Rounded.Search, label = "Research")
            SuggestionChip(icon = Icons.Rounded.Code, label = "Code")
            SuggestionChip(icon = Icons.Rounded.AutoAwesome, label = "Ideas")
        }
    }
}

@Composable
private fun SuggestionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        },
    )
}

@Composable
private fun MessageTimeline(
    state: MainUiState,
    listState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(state.messages, key = { it.id }) { message ->
            AvChatBubble(
                roleLabel = "${message.role} • ${message.modelId}",
                content = message.content,
                isUser = message.role == AvRole.USER.name,
            )
        }

        if (state.isSending) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = "Assistant is thinking...",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposerBar(
    prompt: String,
    isSending: Boolean,
    onPromptChanged: (String) -> Unit,
    onSendPrompt: () -> Unit,
    errorMessage: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier.weight(1f),
                    minLines = 1,
                    maxLines = 5,
                    placeholder = { Text("Message AgentVerse") },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )

                FilledIconButton(
                    onClick = onSendPrompt,
                    enabled = !isSending,
                    modifier = Modifier.size(52.dp),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send")
                }
            }
        }

        AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp),
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
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
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

            SettingsTab.CHATS -> ChatPreferencesSection(
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
    AvSectionCard(
        title = "Integrations",
        subtitle = "Connect providers and customize endpoint metadata",
    ) {
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
            label = "App Name",
            value = state.appName,
            onValueChange = onAppNameChanged,
        )
        AvLabeledField(
            label = "Referer (OpenRouter)",
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
    val grandTotal = remember(usage) { usage.sumOf { it.totalTokens }.coerceAtLeast(1L) }

    AvSectionCard(
        title = "Token Analytics",
        subtitle = "Usage is tracked per provider across all conversations",
    ) {
        if (usage.isEmpty()) {
            Text("No token usage recorded yet.")
        } else {
            usage.forEach { row ->
                val progress by remember(row, grandTotal) {
                    derivedStateOf { (row.totalTokens.toFloat() / grandTotal.toFloat()).coerceIn(0f, 1f) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = row.provider.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                    Text(
                        text = "${row.totalTokens} total • ${row.totalRequests} requests • ${row.totalPromptTokens} prompt / ${row.totalCompletionTokens} completion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onResetTokenUsage) {
                Icon(Icons.Rounded.RestartAlt, contentDescription = null)
                Text("  Reset Stats")
            }
        }
    }
}

@Composable
private fun ChatPreferencesSection(
    memorySize: Int,
    streamOutput: Boolean,
    onMemorySizeChanged: (Int) -> Unit,
    onStreamOutputChanged: (Boolean) -> Unit,
) {
    AvSectionCard(
        title = "Chat Behavior",
        subtitle = "Tune memory and response delivery",
    ) {
        Text("Conversation memory: $memorySize messages")
        androidx.compose.material3.Slider(
            value = memorySize.toFloat(),
            onValueChange = { onMemorySizeChanged(it.roundToInt().coerceIn(2, 50)) },
            valueRange = 2f..50f,
        )

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Stream output", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "If enabled, compatible providers can return tokens progressively.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = streamOutput,
                    onCheckedChange = onStreamOutputChanged,
                )
            }
        }
    }
}

private fun formatDate(epochMs: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(epochMs))
