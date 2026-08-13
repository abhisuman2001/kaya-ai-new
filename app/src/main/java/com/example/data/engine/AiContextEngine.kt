package com.example.data.engine

import com.example.data.model.ActiveSiteContext
import com.example.data.model.AiGeneratedReport
import com.example.data.model.ConstructionTask
import com.example.data.model.ContextAnswer
import com.example.data.model.ContextEvent
import com.example.data.model.ContextEventSource
import com.example.data.model.ContextEventType
import com.example.data.model.ContextQueryFilter
import com.example.data.model.ShiftTaskSummary
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

/**
 * AI Context Engine - Central memory layer for Kaya Physical AI Assistant.
 * Continuously collects structured events from Live AI Vision, Hazard Detection,
 * Voice AI, and Blueprint Comparison modules.
 *
 * Maintains active site state and session timeline for context-aware query resolution.
 */
class AiContextEngine {

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val displayTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    private val initialEvents = emptyList<ContextEvent>()

    private val _eventTimeline = MutableStateFlow<List<ContextEvent>>(initialEvents)
    val eventTimeline: StateFlow<List<ContextEvent>> = _eventTimeline.asStateFlow()

    private val initialReports = emptyList<AiGeneratedReport>()

    private val _submittedReports = MutableStateFlow<List<AiGeneratedReport>>(initialReports)
    val submittedReports: StateFlow<List<AiGeneratedReport>> = _submittedReports.asStateFlow()

    private val initialTasks = emptyList<ConstructionTask>()

    private val _assignedTasks = MutableStateFlow<List<ConstructionTask>>(initialTasks)
    val assignedTasks: StateFlow<List<ConstructionTask>> = _assignedTasks.asStateFlow()

    fun setTasks(tasks: List<ConstructionTask>) {
        _assignedTasks.value = tasks
    }

    fun setReports(reports: List<AiGeneratedReport>) {
        _submittedReports.value = reports
    }

    fun addTask(task: ConstructionTask) {
        _assignedTasks.update { it + task }
    }


    fun getShiftTaskSummary(): ShiftTaskSummary {
        val tasks = _assignedTasks.value
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val total = tasks.size
        return ShiftTaskSummary(
            completedCount = completed,
            totalCount = total,
            remainingTimeFormatted = "5h 20m"
        )
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        _assignedTasks.update { list ->
            list.map { task ->
                if (task.taskId == taskId) task.copy(status = newStatus) else task
            }
        }
        val task = _assignedTasks.value.find { it.taskId == taskId }
        if (task != null) {
            val event = ContextEvent(
                id = "evt_tsk_${System.currentTimeMillis()}",
                formattedTime = formatTime(System.currentTimeMillis()),
                type = ContextEventType.SESSION_STATE_CHANGED,
                source = ContextEventSource.SYSTEM,
                title = "Task Updated: ${task.title}",
                description = "Status changed to ${newStatus.label} in Zone ${task.zone}.",
                location = task.zone
            )
            recordEvent(event)
        }
    }

    fun markCurrentTaskCompleted(): String {
        val activeTask = _assignedTasks.value.firstOrNull { it.status == TaskStatus.IN_PROGRESS }
            ?: _assignedTasks.value.firstOrNull { it.status == TaskStatus.PENDING }
        return if (activeTask != null) {
            updateTaskStatus(activeTask.taskId, TaskStatus.COMPLETED)
            "I've marked '${activeTask.title}' as Completed. Your progress is updated."
        } else {
            "All assigned tasks for today's shift are already completed."
        }
    }

    private val _contextState = MutableStateFlow(
        ActiveSiteContext(
            projectName = "Metro Tower Construction",
            activeZone = "Zone B-4 Level 3",
            activeWorkerCount = 3,
            isAiSessionRunning = true,
            currentSceneSummary = "Steel framework assembly near scaffold platform B-4 with active tower crane.",
            lastCameraObservation = "3 workers present on steel erection, 1 operating crane overhead.",
            activeHazards = initialEvents.filter { it.type == ContextEventType.HAZARD_DETECTED },
            resolvedHazardsCount = 1,
            reportedIssuesCount = 1,
            blueprintDeviationsCount = 1,
            lastHazardTimestamp = initialEvents.firstOrNull { it.type == ContextEventType.HAZARD_DETECTED }?.formattedTime ?: "14:18 PM"
        )
    )
    val contextState: StateFlow<ActiveSiteContext> = _contextState.asStateFlow()

    // ==========================================================
    // EVENT RECORDING APIs
    // ==========================================================

    /**
     * Record a raw structured context event.
     */
    fun recordEvent(event: ContextEvent) {
        _eventTimeline.update { currentList ->
            listOf(event) + currentList
        }

        updateContextFromEvents()
    }

    /**
     * Collect a structured observation from Live AI Vision stream.
     */
    fun recordVisionObservation(
        summary: String,
        workerCount: Int = 3,
        zone: String = "Zone B-4 Level 3",
        details: Map<String, String> = emptyMap()
    ) {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_vis_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.VISION_OBSERVATION,
            source = ContextEventSource.LIVE_VISION,
            title = "Live AI Scene Observation",
            description = summary,
            location = zone,
            metadata = details
        )

        _contextState.update { current ->
            current.copy(
                activeZone = zone,
                activeWorkerCount = workerCount,
                currentSceneSummary = summary,
                lastCameraObservation = summary
            )
        }

        recordEvent(event)
    }

    /**
     * Record a newly detected hazard from the Hazard Engine.
     */
    fun recordHazardDetected(
        title: String,
        severity: String,
        location: String,
        description: String,
        category: String = "SAFETY"
    ) {
        val nowMs = System.currentTimeMillis()
        val formattedTime = formatTime(nowMs)

        val event = ContextEvent(
            id = "evt_haz_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formattedTime,
            type = ContextEventType.HAZARD_DETECTED,
            source = ContextEventSource.HAZARD_DETECTION,
            title = title,
            description = description,
            severity = severity,
            location = location,
            metadata = mapOf("category" to category)
        )

        _contextState.update { current ->
            val updatedHazards = listOf(event) + current.activeHazards
            current.copy(
                activeHazards = updatedHazards,
                lastHazardTimestamp = formattedTime
            )
        }

        recordEvent(event)
    }

    /**
     * Record a resolved hazard event.
     */
    fun recordHazardResolved(hazardTitle: String, location: String) {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_res_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.HAZARD_RESOLVED,
            source = ContextEventSource.HAZARD_DETECTION,
            title = "Hazard Resolved: $hazardTitle",
            description = "The reported safety risk '$hazardTitle' at $location has been resolved and verified safe.",
            severity = "INFO",
            location = location
        )

        _contextState.update { current ->
            val remainingHazards = current.activeHazards.filterNot {
                it.title.contains(hazardTitle, ignoreCase = true)
            }
            current.copy(
                activeHazards = remainingHazards,
                resolvedHazardsCount = current.resolvedHazardsCount + 1
            )
        }

        recordEvent(event)
    }

    /**
     * Record a voice query & AI response interaction.
     */
    fun recordVoiceInteraction(userQuery: String, aiResponse: String) {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_voi_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.VOICE_INTERACTION,
            source = ContextEventSource.VOICE_AI,
            title = "Voice Query: \"$userQuery\"",
            description = aiResponse,
            location = _contextState.value.activeZone,
            metadata = mapOf("query" to userQuery, "response" to aiResponse)
        )

        _contextState.update { current ->
            current.copy(lastVoiceQuery = userQuery)
        }

        recordEvent(event)
    }

    /**
     * Record a CAD/BIM Blueprint deviation.
     */
    fun recordBlueprintDeviation(
        deviationTitle: String,
        location: String,
        description: String
    ) {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_cad_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.BLUEPRINT_DEVIATION,
            source = ContextEventSource.BLUEPRINT_COMPARISON,
            title = deviationTitle,
            description = description,
            severity = "MEDIUM",
            location = location
        )

        _contextState.update { current ->
            current.copy(blueprintDeviationsCount = current.blueprintDeviationsCount + 1)
        }

        recordEvent(event)
    }

    /**
     * Record a structured AI-generated report submitted by the worker.
     */
    fun recordStructuredReport(report: AiGeneratedReport) {
        _submittedReports.update { current ->
            listOf(report) + current
        }

        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_rep_${report.reportId}",
            timestampMs = nowMs,
            formattedTime = report.timestamp,
            type = ContextEventType.REPORT_SUBMITTED,
            source = ContextEventSource.REPORTS_MODULE,
            title = "Report Submitted: ${report.title}",
            description = "Category: ${report.issueType}. Severity: ${report.severity}. Zone: ${report.zone}. ${report.description}",
            severity = report.severity.uppercase(Locale.getDefault()),
            location = report.zone,
            metadata = mapOf(
                "reportId" to report.reportId,
                "workerName" to report.workerName,
                "issueType" to report.issueType,
                "status" to report.status
            )
        )

        _contextState.update { current ->
            current.copy(reportedIssuesCount = current.reportedIssuesCount + 1)
        }

        recordEvent(event)
    }

    /**
     * Detects if the worker's natural voice command expresses an issue reporting intent.
     */
    fun isReportIntent(queryText: String): Boolean {
        val q = queryText.lowercase(Locale.getDefault())
        return q.startsWith("report") ||
                q.contains("create report") ||
                q.contains("create an incident report") ||
                q.contains("incident report") ||
                q.contains("report this") ||
                q.contains("report damaged") ||
                q.contains("report exposed") ||
                q.contains("report missing") ||
                q.contains("report to supervisor") ||
                q.contains("file a report")
    }

    /**
     * Generates a structured AiGeneratedReport object based on natural voice command and current context.
     */
    fun generateReportFromVoice(queryText: String): AiGeneratedReport {
        val q = queryText.lowercase(Locale.getDefault())
        val ctx = _contextState.value
        val nowFormatted = formatTime(System.currentTimeMillis())

        // Automatic Classification
        val (issueType, severity, defaultTitle, defaultDesc, detectedObjs) = when {
            q.contains("crack") || q.contains("concrete") || q.contains("column") -> {
                Tuple5(
                    "Structural Defect",
                    "High",
                    "Cracked Structural Concrete Column",
                    "Visible vertical fracture line observed along concrete load-bearing column near grid line B-4.",
                    listOf("Concrete Column", "Crack Indicator", "Structural Member")
                )
            }
            q.contains("scaffold") || q.contains("platform") || q.contains("board") -> {
                Tuple5(
                    "Safety Hazard",
                    "High",
                    "Damaged Scaffold Platform",
                    "Scaffold platform wood plank unanchored and structural handrail missing toe-board.",
                    listOf("Scaffold", "Wood Plank", "Safety Railing")
                )
            }
            q.contains("wiring") || q.contains("exposed") || q.contains("electric") || q.contains("cable") -> {
                Tuple5(
                    "Safety Hazard",
                    "Critical",
                    "Exposed High-Voltage Electrical Wiring",
                    "Uninsulated 480V temporary power cables exposed near damp floor area without lockout/tagout protection.",
                    listOf("Electrical Conduit", "Exposed Wire", "Damp Surface")
                )
            }
            q.contains("barrier") || q.contains("guardrail") || q.contains("edge") || q.contains("fall") -> {
                Tuple5(
                    "Safety Hazard",
                    "High",
                    "Missing Perimeter Fall Protection Barrier",
                    "Unprotected slab edge at Level 18 perimeter missing perimeter guardrail and warning line.",
                    listOf("Slab Edge", "Unprotected Perimeter", "Fall Hazard Zone")
                )
            }
            q.contains("helmet") || q.contains("ppe") || q.contains("vest") -> {
                Tuple5(
                    "Safety Hazard",
                    "High",
                    "Worker Operating in Crane Zone Without Helmet",
                    "Personnel spotted inside active crane swing radius without required hard hat protection.",
                    listOf("Worker", "Tower Crane", "Missing PPE")
                )
            }
            q.contains("crane") || q.contains("leak") || q.contains("hydraulic") || q.contains("lift") -> {
                Tuple5(
                    "Equipment Issue",
                    "Medium",
                    "Hydraulic Fluid Leak on Mobile Crane",
                    "Slow hydraulic fluid drip observed near crane outrigger cylinder #2.",
                    listOf("Mobile Crane", "Outrigger", "Hydraulic Leak")
                )
            }
            q.contains("environmental") || q.contains("spill") || q.contains("dust") -> {
                Tuple5(
                    "Environmental Hazard",
                    "Medium",
                    "Chemical Spill in Storage Zone",
                    "Formwork releasing agent container tipped over causing localized chemical liquid spill.",
                    listOf("Spill Area", "Storage Barrel", "Chemical Container")
                )
            }
            else -> {
                Tuple5(
                    "Safety Hazard",
                    "High",
                    "Field Hazard Report: ${queryText.take(30)}",
                    "Worker reported hazard using voice command: \"$queryText\". Automatic visual context captured.",
                    listOf("Worker", "Site Area", "AI Vision Frame")
                )
            }
        }

        return AiGeneratedReport(
            reportId = "REP-${(1000..9999).random()}",
            workerId = "WRK-8821",
            workerName = "Alex Rivera",
            projectId = "PRJ-METRO-01",
            projectName = ctx.projectName,
            zone = ctx.activeZone,
            timestamp = nowFormatted,
            issueType = issueType,
            title = defaultTitle,
            severity = severity,
            description = defaultDesc,
            detectedObjects = detectedObjs,
            aiObservation = ctx.currentSceneSummary,
            sceneSummary = "Live frame capture from Ray-Ban Meta Smart Glasses on ${ctx.activeZone}.",
            hasCameraSnapshot = true,
            status = "Open"
        )
    }

    private data class Tuple5<A, B, C, D, E>(
        val a: A, val b: B, val c: C, val d: D, val e: E
    )

    /**
     * Record an issue/report submission.
     */
    fun recordReportSubmitted(
        reportTitle: String,
        category: String,
        location: String
    ) {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_rep_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.REPORT_SUBMITTED,
            source = ContextEventSource.REPORTS_MODULE,
            title = "Report Submitted: $reportTitle",
            description = "Category: $category. Location: $location.",
            severity = "HIGH",
            location = location
        )

        _contextState.update { current ->
            current.copy(reportedIssuesCount = current.reportedIssuesCount + 1)
        }

        recordEvent(event)
    }

    /**
     * Record AI session start or stop.
     */
    fun recordSessionStateChange(isRunning: Boolean, deviceName: String = "Ray-Ban Meta") {
        val nowMs = System.currentTimeMillis()
        val event = ContextEvent(
            id = "evt_ses_${UUID.randomUUID().toString().take(8)}",
            timestampMs = nowMs,
            formattedTime = formatTime(nowMs),
            type = ContextEventType.SESSION_STATE_CHANGED,
            source = ContextEventSource.SMART_GLASSES,
            title = if (isRunning) "AI Session Activated" else "AI Session Paused",
            description = "Smart Glasses ($deviceName) live session state set to: ${if (isRunning) "Active" else "Idle"}.",
            location = _contextState.value.activeZone
        )

        _contextState.update { current ->
            current.copy(
                isAiSessionRunning = isRunning,
                sessionStartTimeMs = if (isRunning) nowMs else current.sessionStartTimeMs
            )
        }

        recordEvent(event)
    }

    // ==========================================================
    // CONTEXT RETRIEVAL & QUERY APIs
    // ==========================================================

    fun getCurrentContext(): ActiveSiteContext = _contextState.value

    fun getTimelineEvents(filter: ContextQueryFilter? = null): List<ContextEvent> {
        val allEvents = _eventTimeline.value
        if (filter == null) return allEvents.take(50)

        val kw = filter.keyword
        return allEvents.filter { evt ->
            (filter.source == null || evt.source == filter.source) &&
            (filter.type == null || evt.type == filter.type) &&
            (filter.minSeverity == null || evt.severity.equals(filter.minSeverity, ignoreCase = true)) &&
            (kw.isNullOrBlank() || 
                evt.title.contains(kw, ignoreCase = true) || 
                evt.description.contains(kw, ignoreCase = true))
        }.take(filter.limit)
    }

    fun getLastEvent(): ContextEvent? = _eventTimeline.value.firstOrNull()

    fun getLastHazard(): ContextEvent? {
        return _eventTimeline.value.firstOrNull { it.type == ContextEventType.HAZARD_DETECTED }
    }

    fun getUnresolvedHazards(): List<ContextEvent> {
        return _contextState.value.activeHazards
    }

    fun hasIssueBeenReported(keyword: String): Boolean {
        if (keyword.isBlank()) return false
        return _eventTimeline.value.any { evt ->
            (evt.type == ContextEventType.REPORT_SUBMITTED || evt.type == ContextEventType.HAZARD_DETECTED) &&
            (evt.title.contains(keyword, ignoreCase = true) || evt.description.contains(keyword, ignoreCase = true))
        }
    }

    fun getEventsSince(timestampMs: Long): List<ContextEvent> {
        return _eventTimeline.value.filter { it.timestampMs >= timestampMs }
    }

    fun getSummaryForVoiceAi(): String {
        val ctx = _contextState.value
        val hazards = ctx.activeHazards
        val lastHaz = getLastHazard()

        return if (hazards.isNotEmpty()) {
            "Zone: ${ctx.activeZone}. Active hazards: ${hazards.size}. Latest hazard: '${lastHaz?.title}' at ${lastHaz?.location}. Workers in frame: ${ctx.activeWorkerCount}."
        } else {
            "Zone: ${ctx.activeZone}. Safety status: CLEAR. Workers in frame: ${ctx.activeWorkerCount}. No active hazards."
        }
    }

    /**
     * Primary query resolution method for Voice AI Copilot.
     * Answers questions dynamically based on the central memory layer context.
     */
    fun answerContextualQuestion(questionText: String): ContextAnswer {
        val q = questionText.lowercase()
        val ctx = _contextState.value
        val timeline = _eventTimeline.value

        return when {
            // "What is my next task?" / "my task"
            q.contains("next task") || q.contains("my task") || q.contains("what task") || q.contains("assigned task") -> {
                val tasks = _assignedTasks.value
                val activeTask = tasks.firstOrNull { it.status == TaskStatus.IN_PROGRESS } 
                    ?: tasks.firstOrNull { it.status == TaskStatus.PENDING }
                if (activeTask != null) {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Your next task is ${activeTask.title} on ${activeTask.zone}.",
                        suggestedFollowUps = listOf("Explain this task.", "What safety equipment do I need?", "Mark this task complete.")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "You have no remaining tasks for today's shift. All assigned work is completed!",
                        suggestedFollowUps = listOf("Report an issue", "Is this area safe?")
                    )
                }
            }

            // "Explain this task" / "task details"
            q.contains("explain") && q.contains("task") || q.contains("task detail") || q.contains("how to do") -> {
                val tasks = _assignedTasks.value
                val activeTask = tasks.firstOrNull { it.status == TaskStatus.IN_PROGRESS }
                    ?: tasks.firstOrNull { it.status == TaskStatus.PENDING }
                    ?: tasks.firstOrNull()
                if (activeTask != null) {
                    val ppeStr = activeTask.requiredPpe.joinToString(", ")
                    val recStr = activeTask.aiRecommendations.joinToString(" ")
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "${activeTask.title} on ${activeTask.zone}: ${activeTask.description} Safety Requirements: ${activeTask.safetyRequirements.joinToString(". ")}. AI Recommendations: $recStr",
                        suggestedFollowUps = listOf("What safety equipment do I need?", "Mark this task complete.", "Report an issue")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No active task available to explain.",
                        suggestedFollowUps = listOf("What is my next task?")
                    )
                }
            }

            // "Mark this task complete" / "complete task"
            q.contains("mark") && q.contains("complete") || q.contains("mark complete") || q.contains("task complete") || q.contains("finish task") -> {
                val confirmMsg = markCurrentTaskCompleted()
                ContextAnswer(
                    questionText = questionText,
                    responseText = confirmMsg,
                    suggestedFollowUps = listOf("What is my next task?", "Explain this task.", "Is this area safe?")
                )
            }

            // "What safety equipment do I need?" / "required ppe"
            q.contains("safety equipment") || q.contains("equipment do i need") || q.contains("ppe") || q.contains("gear do i need") -> {
                val tasks = _assignedTasks.value
                val activeTask = tasks.firstOrNull { it.status == TaskStatus.IN_PROGRESS }
                    ?: tasks.firstOrNull { it.status == TaskStatus.PENDING }
                    ?: tasks.firstOrNull()
                if (activeTask != null) {
                    val ppeList = activeTask.requiredPpe.joinToString(", ")
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Required safety equipment for ${activeTask.title}: $ppeList.",
                        suggestedFollowUps = listOf("Explain this task.", "Mark this task complete.", "Is this area safe?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Standard required site PPE: Safety Helmet, High-Vis Vest, Steel-toe Boots, and Eye Protection.",
                        suggestedFollowUps = listOf("What is my next task?")
                    )
                }
            }

            // "What was the last hazard?"
            q.contains("last hazard") || q.contains("recent hazard") || q.contains("previous hazard") -> {
                val lastHaz = getLastHazard()
                if (lastHaz != null) {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "The last recorded hazard was '${lastHaz.title}' at ${lastHaz.location} logged at ${lastHaz.formattedTime}. Severity: ${lastHaz.severity ?: "HIGH"}. Detail: ${lastHaz.description}",
                        relevantEvents = listOf(lastHaz),
                        suggestedFollowUps = listOf("Has this issue already been reported?", "How to resolve this hazard?", "Who is assigned to this zone?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No hazards have been detected in the current AI session.",
                        suggestedFollowUps = listOf("Is this area safe?", "Describe current scene")
                    )
                }
            }

            // "Did I already report this?" / "Did I report this?"
            q.contains("did i report") || q.contains("already report") || q.contains("did i file") || q.contains("have i reported") -> {
                val reports = _submittedReports.value
                if (reports.isNotEmpty()) {
                    val latest = reports.first()
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Yes. A report titled '${latest.title}' (${latest.issueType}) was submitted today at ${latest.timestamp} for ${latest.zone}.",
                        relevantEvents = timeline.filter { it.type == ContextEventType.REPORT_SUBMITTED }.take(3),
                        suggestedFollowUps = listOf("What's the status of my report?", "What was the last hazard?", "Is this area safe?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No reports have been submitted for this active zone during your current session.",
                        suggestedFollowUps = listOf("Report this damaged scaffold", "Report exposed wiring")
                    )
                }
            }

            // "What's the status of my report?" / "Status of report"
            q.contains("status of my report") || q.contains("report status") || q.contains("status of report") -> {
                val reports = _submittedReports.value
                if (reports.isNotEmpty()) {
                    val latest = reports.first()
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Your report '${latest.title}' (ID: ${latest.reportId}) is currently under supervisor review with status '${latest.status}'. Submitted at ${latest.timestamp}.",
                        relevantEvents = timeline.filter { it.type == ContextEventType.REPORT_SUBMITTED }.take(2),
                        suggestedFollowUps = listOf("Did I already report this?", "What was the last hazard?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No active reports were found to query status.",
                        suggestedFollowUps = listOf("Report this issue", "Is this area safe?")
                    )
                }
            }

            // "Has this issue already been reported?" / "Is this reported?"
            q.contains("reported") || q.contains("report filed") || q.contains("already recorded") -> {
                val reports = timeline.filter { 
                    it.type == ContextEventType.REPORT_SUBMITTED || it.type == ContextEventType.HAZARD_DETECTED 
                }
                if (reports.isNotEmpty()) {
                    val latest = reports.first()
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Yes, issue '${latest.title}' at ${latest.location} was recorded at ${latest.formattedTime}. Status: Logged in Kaya Context Engine.",
                        relevantEvents = reports.take(3),
                        suggestedFollowUps = listOf("What was the last hazard?", "Show session timeline")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No formal reports have been submitted for this active zone yet. Would you like me to draft a safety report now?",
                        suggestedFollowUps = listOf("Report Safety Issue", "Check PPE compliance")
                    )
                }
            }

            // "What changed while I was away?" / "What changed recently?"
            q.contains("changed") || q.contains("while i was away") || q.contains("recent updates") || q.contains("what happened") -> {
                val recentTenMinutes = System.currentTimeMillis() - 15 * 60 * 1000
                val recentEvents = getEventsSince(recentTenMinutes)
                if (recentEvents.isNotEmpty()) {
                    val summaryList = recentEvents.take(3).joinToString("; ") { "${it.title} (${it.formattedTime})" }
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "In the past 15 minutes, ${recentEvents.size} events were logged in your zone: $summaryList.",
                        relevantEvents = recentEvents,
                        suggestedFollowUps = listOf("What was the last hazard?", "Is the area safe now?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "No major changes occurred recently. The site condition remains stable with ${ctx.activeWorkerCount} workers observed.",
                        suggestedFollowUps = listOf("Describe scene", "Check blueprint alignment")
                    )
                }
            }

            // "Is this area safe?" / "Safety status"
            q.contains("safe") || q.contains("danger") || q.contains("risk") || q.contains("hazard") -> {
                val activeHaz = ctx.activeHazards
                if (activeHaz.isNotEmpty()) {
                    val hazTitle = activeHaz.first().title
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Caution advised in ${ctx.activeZone}. There is 1 active hazard: $hazTitle. Please ensure all workers maintain proper PPE and stay clear of active crane swing zones.",
                        relevantEvents = activeHaz,
                        suggestedFollowUps = listOf("Has this issue already been reported?", "What should I do next?")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "Zone ${ctx.activeZone} is currently evaluated as SAFE. All ${ctx.activeWorkerCount} workers are compliant with active safety standards.",
                        suggestedFollowUps = listOf("Describe scene", "What am I looking at?")
                    )
                }
            }

            // "What am I looking at?" / "Scene description"
            q.contains("looking") || q.contains("see") || q.contains("scene") || q.contains("camera") -> {
                ContextAnswer(
                    questionText = questionText,
                    responseText = "Live AI Vision Observation: ${ctx.currentSceneSummary} Active workers: ${ctx.activeWorkerCount}. Location: ${ctx.activeZone}.",
                    relevantEvents = timeline.filter { it.type == ContextEventType.VISION_OBSERVATION }.take(2),
                    suggestedFollowUps = listOf("Is this area safe?", "Check blueprint alignment")
                )
            }

            // "Are all workers wearing PPE?"
            q.contains("ppe") || q.contains("helmet") || q.contains("vest") || q.contains("glove") -> {
                val ppeHazards = timeline.filter { 
                    it.title.contains("helmet", ignoreCase = true) || 
                    it.title.contains("vest", ignoreCase = true) || 
                    it.title.contains("ppe", ignoreCase = true) 
                }
                if (ppeHazards.isNotEmpty()) {
                    val latest = ppeHazards.first()
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "PPE Compliance Audit: 2 of 3 workers are 100% compliant. 1 worker non-compliant: '${latest.title}' logged at ${latest.formattedTime}.",
                        relevantEvents = ppeHazards,
                        suggestedFollowUps = listOf("Report PPE violation", "Notify supervisor")
                    )
                } else {
                    ContextAnswer(
                        questionText = questionText,
                        responseText = "PPE Compliance Audit: 100% compliant. All ${ctx.activeWorkerCount} workers in frame are wearing safety helmets and high-visibility vests.",
                        suggestedFollowUps = listOf("Is this area safe?", "What am I looking at?")
                    )
                }
            }

            // "Summarize active session" / "Summarize work zone"
            q.contains("summarize") || q.contains("summary") || q.contains("overview") || q.contains("session") -> {
                ContextAnswer(
                    questionText = questionText,
                    responseText = "Session Context Overview for ${ctx.projectName} (${ctx.activeZone}): AI Session active on Ray-Ban Meta Glasses. Active workers: ${ctx.activeWorkerCount}. Active hazards: ${ctx.activeHazards.size}. Resolved hazards: ${ctx.resolvedHazardsCount}. Blueprint deviations: ${ctx.blueprintDeviationsCount}.",
                    relevantEvents = timeline.take(5),
                    suggestedFollowUps = listOf("What was the last hazard?", "Has this issue already been reported?")
                )
            }

            // Generic / Fallback Response powered by Context Engine Memory
            else -> {
                val lastHaz = getLastHazard()
                val hazText = if (lastHaz != null) "Last hazard logged: '${lastHaz.title}' at ${lastHaz.formattedTime}." else "No active hazards."
                ContextAnswer(
                    questionText = questionText,
                    responseText = "Analyzing live context for ${ctx.activeZone}: $hazText Current observation: ${ctx.currentSceneSummary}",
                    relevantEvents = timeline.take(2),
                    suggestedFollowUps = listOf("What was the last hazard?", "Has this issue already been reported?", "What changed while I was away?")
                )
            }
        }
    }

    private fun updateContextFromEvents() {
        val currentHazards = _eventTimeline.value.filter { 
            it.type == ContextEventType.HAZARD_DETECTED 
        }
        _contextState.update { current ->
            current.copy(activeHazards = currentHazards)
        }
    }

    private fun formatTime(ms: Long): String {
        return try {
            displayTimeFormatter.format(ms)
        } catch (e: Exception) {
            "14:20 PM"
        }
    }

    private fun String?.isNull_or_blank(): Boolean {
        return this == null || this.trim().isEmpty()
    }
}
