package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hazards")
data class HazardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // PPE, Fall Risk, Crane Proximity, Electrical, Scaffolding
    val severity: String, // CRITICAL, HIGH, MEDIUM, LOW
    val location: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val actionTaken: String = "",
    val assignedWorkerId: String? = null,
    val assignedWorkerName: String? = null,
    val syncStatus: String = "SYNCED" // "SYNCED", "PENDING_SYNC"
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // DPR, SAFETY, NCR, PROGRESS
    val title: String,
    val summary: String,
    val crewCount: Int = 18,
    val hazardsFound: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUBMITTED",
    val syncStatus: String = "SYNCED"
)

@Entity(tableName = "blueprints")
data class BlueprintEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val title: String,
    val type: String, // ARCHITECTURAL, STRUCTURAL, MEP, GFC
    val revision: String,
    val lastSynced: Long = System.currentTimeMillis(),
    val deviationCount: Int = 0
)

@Entity(tableName = "knowledge")
data class KnowledgeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // SAFETY_MANUAL, BIM_DRAWING, SOP, CODE, BOQ
    val contentSnippet: String,
    val tags: String
)
