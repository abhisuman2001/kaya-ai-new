package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiGeneratedReport
import com.example.data.model.GlassAiState
import com.example.ui.components.ReportConfirmationBottomSheet
import com.example.ui.theme.MetaBlue
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.KayaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// VOICE CONVERSATION DATA MODELS
// ==========================================

enum class VoiceState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

data class VoiceChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val relatedHazardTitle: String? = null,
    val isTtsPlaying: Boolean = false
)

// ==========================================
// VOICE AI COPILOT SCREEN
// ==========================================

@Composable
fun AiAssistantScreen(
    viewModel: KayaViewModel,
    modifier: Modifier = Modifier
) {
    val glassState by viewModel.glassState.collectAsStateWithLifecycle()
    val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
    val isGlassConnected = glassState.connectionState != GlassAiState.OFFLINE
    val isLiveStreaming = glassState.isLiveStreaming

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = java.util.Locale.US
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    var voiceState by remember { mutableStateOf(VoiceState.IDLE) }
    var liveTranscriptText by remember { mutableStateOf("") }
    var textInputText by remember { mutableStateOf("") }
    var showTextInput by remember { mutableStateOf(false) }

    var activeDraftReport by remember { mutableStateOf<AiGeneratedReport?>(null) }
    var showReportBottomSheet by remember { mutableStateOf(false) }

    val messages = remember { mutableStateListOf<VoiceChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Suggested Questions
    val suggestedQuestions = remember {
        listOf(
            "Report damaged scaffold.",
            "Report exposed wiring.",
            "Report missing safety barrier.",
            "Did I already report this?",
            "What's the status of my report?",
            "Is this area safe?",
            "What am I looking at?",
            "Summarize this work zone."
        )
    }

    // Function to handle submitting a question (from Voice, Suggestion Chip, or Typed input)
    fun processUserQuestion(questionText: String) {
        if (questionText.isBlank()) return

        if (!isGlassConnected) {
            viewModel.connectGlass()
        }

        val timestamp = SimpleTimeFormatter.now()
        val userMsgId = "user_${System.currentTimeMillis()}"

        // Add user message
        messages.add(
            VoiceChatMessage(
                id = userMsgId,
                isUser = true,
                text = questionText,
                timestamp = timestamp
            )
        )

        // Clear input state
        textInputText = ""
        liveTranscriptText = questionText

        val lowerQ = questionText.lowercase()
        val isHazardFilingCommand = lowerQ.contains("file hazard") || 
                                    lowerQ.contains("log hazard") || 
                                    lowerQ.contains("report hazard") || 
                                    lowerQ.contains("create hazard") || 
                                    lowerQ.contains("hazard alert")

        // Check if query is hands-free report creation intent
        val isReporting = viewModel.aiContextEngine.isReportIntent(questionText)
        var voiceFiledHazardTitle: String? = null

        if (isHazardFilingCommand) {
            val (filedItem, _) = viewModel.processVoiceHazardCommand(questionText)
            voiceFiledHazardTitle = filedItem?.title
        } else if (isReporting) {
            val draft = viewModel.aiContextEngine.generateReportFromVoice(questionText)
            activeDraftReport = draft
            showReportBottomSheet = true
        }

        // Scroll to latest
        coroutineScope.launch {
            delay(100)
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        // AI Voice Processing Cycle: Listening -> Thinking -> Speaking -> Idle
        coroutineScope.launch {
            voiceState = VoiceState.LISTENING
            delay(600)

            voiceState = VoiceState.THINKING
            
            // Run real AI analysis using Gemini API or Context Engine
            viewModel.runAiQuery(questionText)
            delay(1000)

            val aiResponseText = if (isHazardFilingCommand) {
                val lastHz = viewModel.hazardDetectionState.value.lastVoiceFiledHazard
                if (lastHz != null) {
                    "Hazard observation filed via Voice Command: '${lastHz.title}' at ${lastHz.location} (${lastHz.severity} severity). Logged under ${lastHz.oshaStandard} and synced to the safety database."
                } else {
                    "I've processed your voice command and logged the hazard observation into the Kaya safety matrix."
                }
            } else if (isReporting && activeDraftReport != null) {
                val draft = activeDraftReport!!
                "I've captured the current camera frame and context, classified the issue as '${draft.issueType}' (${draft.severity} severity), and generated a draft report for '${draft.title}'. Please review and confirm."
            } else {
                val contextAnswer = viewModel.aiContextEngine.answerContextualQuestion(questionText)
                val liveCameraObs = liveResult?.aiResponseText
                if (!liveCameraObs.isNullOrBlank() && (questionText.contains("see", ignoreCase = true) || questionText.contains("camera", ignoreCase = true) || questionText.contains("look", ignoreCase = true) || questionText.contains("scene", ignoreCase = true) || questionText.contains("view", ignoreCase = true))) {
                    "Observing camera feed: $liveCameraObs. ${contextAnswer.responseText}"
                } else {
                    contextAnswer.responseText
                }
            }

            viewModel.aiContextEngine.recordVoiceInteraction(questionText, aiResponseText)

            val aiMsgId = "ai_${System.currentTimeMillis()}"

            messages.add(
                VoiceChatMessage(
                    id = aiMsgId,
                    isUser = false,
                    text = aiResponseText,
                    timestamp = SimpleTimeFormatter.now(),
                    relatedHazardTitle = voiceFiledHazardTitle
                )
            )

            voiceState = VoiceState.SPEAKING
            // Speak output voice through the mobile phone speaker (acting as Meta Glass speaker)
            tts?.speak(aiResponseText, TextToSpeech.QUEUE_FLUSH, null, "voice_ai_output")
            delay(2800)

            voiceState = VoiceState.IDLE
            liveTranscriptText = ""

            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Handle Microphone button tap
    fun toggleMicrophone() {
        if (!isGlassConnected) {
            viewModel.connectGlass()
            return
        }
        when (voiceState) {
            VoiceState.IDLE -> {
                voiceState = VoiceState.LISTENING
                liveTranscriptText = "Listening to your voice..."
                coroutineScope.launch {
                    delay(2000)
                    val simulatedVoiceQuery = "Is this work zone safe for crane operation?"
                    processUserQuestion(simulatedVoiceQuery)
                }
            }
            VoiceState.LISTENING, VoiceState.THINKING, VoiceState.SPEAKING -> {
                voiceState = VoiceState.IDLE
                liveTranscriptText = ""
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("voice_ai_conversation_list"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // 1. Header
                item {
                    Column {
                        Text(
                            text = "VOICE AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetaBlue,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Voice AI",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Talk naturally with your AI Safety Copilot",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isGlassConnected) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voice_ai_glass_disconnected_card"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MicOff,
                                        contentDescription = null,
                                        tint = StatusError,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Text(
                                        text = "Smart Glasses Disconnected",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Voice AI features require an active connection to your Ray-Ban Meta open-ear speakers & microphone array.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.connectGlass() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MetaBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .testTag("connect_glass_from_voice_ai_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BluetoothConnected,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Connect Smart Glasses",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. AI Status Card
                item {
                    AiStatusCard(
                        isLiveStreaming = isLiveStreaming,
                        onStartLiveAi = { viewModel.toggleLiveStream() }
                    )
                }

                // AI API Fallback Alert (when API key is invalid/expired)
                if (liveResult.isApiError) {
                    item {
                        AiApiFallbackAlertCard(
                            errorMessage = liveResult.apiErrorMessage,
                            onRetry = { viewModel.runAiQuery("Test Gemini API token status") }
                        )
                    }
                }



                // 3. Empty State (When no messages yet)
                if (messages.isEmpty()) {
                    item {
                        EmptyVoiceAiState()
                    }

                    // 4. Suggested Questions Chips
                    item {
                        SuggestedQuestionsSection(
                            suggestions = suggestedQuestions,
                            onSuggestionClick = { question -> processUserQuestion(question) }
                        )
                    }
                } else {
                    // Conversation Messages List
                    itemsIndexed(messages, key = { _, msg -> msg.id }) { _, message ->
                        ConversationMessageItem(
                            message = message,
                            onToggleTts = { msgId ->
                                val index = messages.indexOfFirst { it.id == msgId }
                                if (index != -1) {
                                    val current = messages[index]
                                    val newTtsPlaying = !current.isTtsPlaying
                                    messages[index] = current.copy(isTtsPlaying = newTtsPlaying)
                                    if (newTtsPlaying) {
                                        tts?.speak(current.text, TextToSpeech.QUEUE_FLUSH, null, "voice_msg_speak")
                                    } else {
                                        tts?.stop()
                                    }
                                }
                            }
                        )
                    }

                    // 5. Live Transcript / AI Thinking Animation Item
                    if (voiceState != VoiceState.IDLE) {
                        item {
                            ActiveVoiceProcessingBanner(
                                voiceState = voiceState,
                                liveTranscript = liveTranscriptText
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(110.dp)) } // Spacer for bottom floating mic control
            }
        }

        // 6. Bottom Floating Voice Control Bar
        BottomVoiceControlBar(
            voiceState = voiceState,
            showTextInput = showTextInput,
            textInputText = textInputText,
            onTextInputChange = { textInputText = it },
            onToggleTextInput = { showTextInput = !showTextInput },
            onSendText = {
                processUserQuestion(textInputText)
            },
            onMicClick = { toggleMicrophone() },
            onClearConversation = {
                messages.clear()
                voiceState = VoiceState.IDLE
                liveTranscriptText = ""
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // Hands-Free Report Confirmation Bottom Sheet
        if (showReportBottomSheet && activeDraftReport != null) {
            ReportConfirmationBottomSheet(
                report = activeDraftReport!!,
                onDismiss = {
                    showReportBottomSheet = false
                },
                onSubmit = { submittedReport ->
                    viewModel.aiContextEngine.recordStructuredReport(submittedReport)
                    val confirmMsg = "✓ I've created the report for '${submittedReport.title}' (${submittedReport.issueType}) with current camera evidence and notified your supervisor."
                    messages.add(
                        VoiceChatMessage(
                            id = "ai_sub_${System.currentTimeMillis()}",
                            isUser = false,
                            text = confirmMsg,
                            timestamp = SimpleTimeFormatter.now()
                        )
                    )
                    viewModel.aiContextEngine.recordVoiceInteraction(
                        userQuery = "Confirm Report: ${submittedReport.title}",
                        aiResponse = "I've created the report and notified your supervisor."
                    )
                }
            )
        }
    }
}

// ==========================================
// AI STATUS CARD
// ==========================================

@Composable
fun AiStatusCard(
    isLiveStreaming: Boolean,
    onStartLiveAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_ai_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLiveStreaming) {
                StatusSuccess.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLiveStreaming) StatusSuccess.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isLiveStreaming) StatusSuccess.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isLiveStreaming) StatusSuccess else Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isLiveStreaming) "🟢 AI Connected" else "⚪ Waiting for Vision Context",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLiveStreaming) StatusSuccess else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isLiveStreaming) {
                            "Live AI Session Active • Using Real-Time Site Context"
                        } else {
                            "Start a Live AI Session to enable contextual answers."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isLiveStreaming) {
                Button(
                    onClick = onStartLiveAi,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MetaBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("start_live_ai_button")
                ) {
                    Text("Start Live AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// EMPTY VOICE AI STATE
// ==========================================

@Composable
fun EmptyVoiceAiState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("empty_voice_ai_state"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MetaBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👋", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hello! I'm your AI Safety Copilot",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ask me anything about your surroundings. I can explain hazards, describe the site, and answer questions using Live AI Vision.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// ==========================================
// SUGGESTED QUESTIONS CHIPS
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestedQuestionsSection(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MetaBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Suggested Questions",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { question ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.6f)),
                    modifier = Modifier
                        .clickable { onSuggestionClick(question) }
                        .testTag("suggestion_chip_$question")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MetaBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = question,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CONVERSATION MESSAGE ITEM
// ==========================================

@Composable
fun ConversationMessageItem(
    message: VoiceChatMessage,
    onToggleTts: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (message.isUser) "user_message_item" else "ai_message_item"),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (message.isUser) {
            // User Message (Right Aligned)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                    color = MetaBlue,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You • ${message.timestamp}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        } else {
            // AI Copilot Message (Left Aligned - Gemini Style)
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MetaBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MetaBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Safety Copilot",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetaBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${message.timestamp}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = message.text,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row (TTS to Smart Glasses, Copy, Feedback)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (message.isTtsPlaying) StatusSuccess.copy(0.15f) else MetaBlue.copy(0.12f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    if (message.isTtsPlaying) StatusSuccess else MetaBlue.copy(0.4f)
                                ),
                                modifier = Modifier.clickable { onToggleTts(message.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (message.isTtsPlaying) Icons.Default.VolumeUp else Icons.Default.GraphicEq,
                                        contentDescription = "Speak to glasses",
                                        tint = if (message.isTtsPlaying) StatusSuccess else MetaBlue,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (message.isTtsPlaying) "SPEAKING TO GLASSES..." else "PLAY TO GLASSES",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (message.isTtsPlaying) StatusSuccess else MetaBlue
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(message.text))
                                        isCopied = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy message",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Helpful",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ACTIVE VOICE PROCESSING BANNER
// ==========================================

@Composable
fun ActiveVoiceProcessingBanner(
    voiceState: VoiceState,
    liveTranscript: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_wave_banner")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_voice_processing_banner"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MetaBlue.copy(0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (voiceState) {
                            VoiceState.LISTENING -> StatusError.copy(alpha = 0.2f)
                            VoiceState.THINKING -> MetaBlue.copy(alpha = 0.2f)
                            VoiceState.SPEAKING -> StatusSuccess.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (voiceState) {
                        VoiceState.LISTENING -> Icons.Default.Mic
                        VoiceState.THINKING -> Icons.Default.Psychology
                        VoiceState.SPEAKING -> Icons.Default.VolumeUp
                        else -> Icons.Default.RecordVoiceOver
                    },
                    contentDescription = null,
                    tint = when (voiceState) {
                        VoiceState.LISTENING -> StatusError
                        VoiceState.THINKING -> MetaBlue
                        VoiceState.SPEAKING -> StatusSuccess
                        else -> MetaBlue
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (voiceState) {
                        VoiceState.LISTENING -> "🔴 Listening..."
                        VoiceState.THINKING -> "✨ AI is thinking..."
                        VoiceState.SPEAKING -> "🔊 AI Responding..."
                        else -> "Processing..."
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (liveTranscript.isNotBlank()) liveTranscript else "Processing voice audio from Ray-Ban Meta Glasses...",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Animated Waveform Canvas
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(24.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val barWidth = 4.dp.toPx()
                    val gap = 4.dp.toPx()

                    val barHeights = listOf(
                        height * (0.3f + 0.4f * pulseAlpha),
                        height * (0.7f - 0.3f * pulseAlpha),
                        height * (0.4f + 0.5f * pulseAlpha),
                        height * (0.8f - 0.4f * pulseAlpha)
                    )

                    barHeights.forEachIndexed { i, barH ->
                        val left = i * (barWidth + gap)
                        val top = (height - barH) / 2f
                        drawRoundRect(
                            color = MetaBlue.copy(alpha = pulseAlpha),
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// BOTTOM VOICE CONTROL BAR
// ==========================================

@Composable
fun BottomVoiceControlBar(
    voiceState: VoiceState,
    showTextInput: Boolean,
    textInputText: String,
    onTextInputChange: (String) -> Unit,
    onToggleTextInput: () -> Unit,
    onSendText: () -> Unit,
    onMicClick: () -> Unit,
    onClearConversation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = modifier.testTag("bottom_voice_control_bar"),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Optional Text Input Box
            AnimatedVisibility(visible = showTextInput) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInputText,
                        onValueChange = onTextInputChange,
                        placeholder = { Text("Ask AI Safety Copilot...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_text_input_field"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSendText() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MetaBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSendText,
                        enabled = textInputText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (textInputText.isNotBlank()) MetaBlue else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("send_text_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (textInputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Main Voice Bar Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Keyboard Toggle Button
                IconButton(
                    onClick = onToggleTextInput,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (showTextInput) MetaBlue.copy(0.15f) else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("keyboard_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Toggle text input",
                        tint = if (showTextInput) MetaBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Center: One Large Microphone Action Button
                Box(contentAlignment = Alignment.Center) {
                    // Pulsing Outer Glow Ring when active
                    if (voiceState == VoiceState.LISTENING) {
                        Box(
                            modifier = Modifier
                                .size((68 * pulseScale).dp)
                                .clip(CircleShape)
                                .background(StatusError.copy(alpha = 0.25f))
                        )
                    }

                    Surface(
                        onClick = onMicClick,
                        shape = CircleShape,
                        color = when (voiceState) {
                            VoiceState.LISTENING -> StatusError
                            VoiceState.THINKING -> MetaBlue
                            VoiceState.SPEAKING -> StatusSuccess
                            VoiceState.IDLE -> MetaBlue
                        },
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(60.dp)
                            .testTag("main_microphone_button")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = when (voiceState) {
                                    VoiceState.LISTENING -> Icons.Default.Mic
                                    VoiceState.THINKING -> Icons.Default.AutoAwesome
                                    VoiceState.SPEAKING -> Icons.Default.VolumeUp
                                    VoiceState.IDLE -> Icons.Default.Mic
                                },
                                contentDescription = "Voice Input",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Right Side: Clear Conversation Button
                IconButton(
                    onClick = onClearConversation,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("clear_conversation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear session history",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // State Label below Microphone
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (voiceState) {
                    VoiceState.IDLE -> "Tap to speak with AI Safety Copilot"
                    VoiceState.LISTENING -> "🔴 Listening... (Tap to stop)"
                    VoiceState.THINKING -> "✨ AI is thinking..."
                    VoiceState.SPEAKING -> "🔊 Responding via Ray-Ban Glasses"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = when (voiceState) {
                    VoiceState.LISTENING -> StatusError
                    VoiceState.SPEAKING -> StatusSuccess
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

// ==========================================
// CONTEXTUAL AI RESPONSE GENERATOR
// ==========================================

private fun generateContextualAiResponse(question: String, isLiveStreaming: Boolean): String {
    val q = question.lowercase()

    return when {
        q.contains("safe") || q.contains("hazard") || q.contains("danger") || q.contains("risk") -> {
            "The work zone is mostly safe for general activity. However, 1 critical hazard is present: a worker near the tower crane swing zone is missing a safety helmet. I recommend ensuring proper PPE before crane lifts resume."
        }
        q.contains("looking") || q.contains("see") || q.contains("view") || q.contains("camera") -> {
            "You are observing a steel framework assembly operation on Level 3 near scaffold platform B-4. There are 3 workers present, 1 active tower crane, and elevated scaffolding."
        }
        q.contains("explain") || q.contains("why") || q.contains("violation") || q.contains("helmet") -> {
            "A worker has entered the active crane operating area without head protection. Why this is risky: falling objects or equipment movement pose an immediate head injury hazard. Recommended action: wear an approved safety helmet immediately."
        }
        q.contains("next") || q.contains("fix") || q.contains("action") || q.contains("do") -> {
            "Immediate actions: 1. Instruct worker #1 near grid B-4 to equip safety helmet. 2. Verify dual-leg lanyard tie-offs on scaffold platform 3. 3. Confirm crane operator clearance."
        }
        q.contains("summarize") || q.contains("summary") || q.contains("zone") || q.contains("scene") -> {
            "Work Zone Summary: 3 workers present on steel erection. 1 tower crane operating overhead, 1 scaffold active. 2 workers fully compliant, 1 PPE violation flagged."
        }
        q.contains("ppe") || q.contains("vest") || q.contains("glove") -> {
            "PPE Status: 2 out of 3 workers are 100% compliant with helmets and safety vests. 1 worker near the crane zone is missing a safety helmet."
        }
        q.contains("condition") || q.contains("today") || q.contains("weather") -> {
            "Site conditions are optimal with clear visibility (1080p stream active). Ambient temp is 31°C. Structural steel work is progressing smoothly with 1 active PPE risk monitored."
        }
        else -> {
            "I'm analyzing your surroundings via Live AI Vision. Currently, 3 workers and 1 operating crane are in frame. One worker near the crane requires a safety helmet. Let me know if you need specific safety checks!"
        }
    }
}

private object SimpleTimeFormatter {
    fun now(): String {
        val currentTime = java.util.Calendar.getInstance()
        val hour = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(java.util.Calendar.MINUTE)
        val amPm = if (hour >= 12) "PM" else "AM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%02d:%02d %s", hour12, minute, amPm)
    }
}
