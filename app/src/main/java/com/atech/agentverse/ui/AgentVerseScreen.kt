package com.atech.agentverse.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PersonAddAlt1
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atech.agentverse.presentation.MainUiState
import com.atech.agentverse.presentation.ScreenDestination
import com.atech.agentverse.presentation.SettingsTab
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.core.model.ProviderUsageSummary
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
    val isChatScreen = state.activeScreen == ScreenDestination.CHAT

    val pageBackground = Brush.verticalGradient(
        colors = if (isChatScreen) {
            listOf(
                Color(0xFF020202),
                Color(0xFF060606),
                Color(0xFF020202),
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.background,
            )
        },
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
                containerColor = Color.Transparent,
                topBar = {
                    if (!isChatScreen) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Settings",
                                    maxLines = 1,
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                                }
                            },
                        )
                    }
                },
            ) { innerPadding ->
                when (state.activeScreen) {
                    ScreenDestination.CHAT -> ChatScreenContent(
                        state = state,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onOpenSettingsScreen = onOpenSettingsScreen,
                        onCreateNewChat = onCreateNewChat,
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
    onOpenDrawer: () -> Unit,
    onOpenSettingsScreen: () -> Unit,
    onCreateNewChat: () -> Unit,
    onPromptChanged: (String) -> Unit,
    onSendPrompt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size, state.streamingAssistantText.length) {
        val extraStreamingRow = if (state.streamingAssistantText.isNotBlank()) 1 else 0
        val targetIndex = (state.messages.size + extraStreamingRow - 1).coerceAtLeast(0)
        if (state.messages.isNotEmpty() || state.streamingAssistantText.isNotBlank()) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        ChatTopBar(
            onOpenDrawer = onOpenDrawer,
            onOpenSettingsScreen = onOpenSettingsScreen,
            onCreateNewChat = onCreateNewChat,
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            if (state.messages.isEmpty() && state.streamingAssistantText.isBlank()) {
                EmptyConversationView(
                    onSuggestionSelected = onPromptChanged,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                MessageTimeline(state = state, listState = listState)
            }
        }

        Spacer(Modifier.height(8.dp))

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
private fun ChatTopBar(
    onOpenDrawer: () -> Unit,
    onOpenSettingsScreen: () -> Unit,
    onCreateNewChat: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Color(0xFF222327),
        ) {
            IconButton(onClick = onOpenDrawer, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Open history",
                    tint = Color(0xFFD9D9DC),
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF17181B),
            border = BorderStroke(1.dp, Color(0xFF2E3034)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(onClick = onOpenSettingsScreen, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.PersonAddAlt1,
                        contentDescription = "Open settings",
                        tint = Color(0xFFD8D8DB),
                    )
                }
                IconButton(onClick = onCreateNewChat, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = "New chat",
                        tint = Color(0xFFD8D8DB),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyConversationView(
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "What can I help with?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFF0F0F2),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(18.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SuggestionActionChip(
                icon = Icons.Rounded.Image,
                label = "Create image",
                iconTint = Color(0xFF6BE48D),
                onClick = { onSuggestionSelected("Create image") },
            )
            SuggestionActionChip(
                icon = Icons.Rounded.Visibility,
                label = "Analyze images",
                iconTint = Color(0xFF7B73FF),
                onClick = { onSuggestionSelected("Analyze images") },
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SuggestionActionChip(
                icon = Icons.Rounded.Code,
                label = "Code",
                iconTint = Color(0xFF7B73FF),
                onClick = { onSuggestionSelected("Help me with code") },
            )
            SuggestionActionChip(
                icon = Icons.Rounded.BarChart,
                label = "Analyze data",
                iconTint = Color(0xFF6CD4FF),
                onClick = { onSuggestionSelected("Analyze this data") },
            )
        }
    }
}

@Composable
private fun SuggestionActionChip(
    icon: ImageVector,
    label: String,
    iconTint: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF08090A),
        border = BorderStroke(1.dp, Color(0xFF222428)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = iconTint,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFFC5C7CC),
            )
        }
    }
}

@Composable
private fun MessageTimeline(
    state: MainUiState,
    listState: LazyListState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp, vertical = 6.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(state.messages, key = { it.id }) { message ->
            ChatMessageBubble(
                roleLabel = "${message.role} • ${message.modelId}",
                content = message.content,
                isUser = message.role == AvRole.USER.name,
            )
        }

        if (state.streamingAssistantText.isNotBlank()) {
            item(key = "streaming-assistant") {
                ChatMessageBubble(
                    roleLabel = "ASSISTANT • ${state.selectedProvider.name} (streaming)",
                    content = state.streamingAssistantText,
                    isUser = false,
                )
            }
        }

        if (state.isSending && state.streamingAssistantText.isBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF111214),
                    border = BorderStroke(1.dp, Color(0xFF232428)),
                ) {
                    Text(
                        text = "Assistant is thinking...",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB5B8BE),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    roleLabel: String,
    content: String,
    isUser: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.92f),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 8.dp,
                bottomEnd = if (isUser) 8.dp else 18.dp,
            ),
            color = if (isUser) Color(0xFF1B2434) else Color(0xFF111214),
            border = if (isUser) null else BorderStroke(1.dp, Color(0xFF27282D)),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = roleLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8D92A0),
                )
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE8E9EC),
                )
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
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF18191D),
            border = BorderStroke(1.dp, Color(0xFF2A2C31)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add attachment",
                        tint = Color(0xFFCFD1D6),
                    )
                }

                TextField(
                    value = prompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Ask ChatGPT",
                            color = Color(0xFF9598A0),
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = Color(0xFFF1F2F4),
                        unfocusedTextColor = Color(0xFFF1F2F4),
                        disabledTextColor = Color(0xFFF1F2F4),
                        cursorColor = Color(0xFFF1F2F4),
                    ),
                )

                IconButton(onClick = {}, enabled = !isSending) {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = "Voice input",
                        tint = Color(0xFFCFD1D6),
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(enabled = !isSending && prompt.isNotBlank(), onClick = onSendPrompt),
                    shape = CircleShape,
                    color = if (prompt.isNotBlank()) Color(0xFFECEEF2) else Color(0xFF3A3C42),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (prompt.isNotBlank()) Icons.AutoMirrored.Rounded.Send else Icons.Rounded.GraphicEq,
                            contentDescription = "Send",
                            tint = if (prompt.isNotBlank()) Color(0xFF16171A) else Color(0xFFA8ABB3),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 8.dp),
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
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val tabs = listOf(
            SettingsTab.INTEGRATION to "Integration",
            SettingsTab.TOKENS to "Tokens",
            SettingsTab.CHATS to "Chats",
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                tabs.forEach { (tab, label) ->
                    val selected = state.settingsTab == tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSettingsTabSelected(tab) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .width(52.dp)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                                    shape = RoundedCornerShape(2.dp),
                                ),
                        )
                    }
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
