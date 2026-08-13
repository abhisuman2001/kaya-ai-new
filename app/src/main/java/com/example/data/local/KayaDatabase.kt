package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HazardEntity::class,
        ReportEntity::class,
        BlueprintEntity::class,
        KnowledgeItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KayaDatabase : RoomDatabase() {
    abstract fun hazardDao(): HazardDao
    abstract fun reportDao(): ReportDao
    abstract fun blueprintDao(): BlueprintDao
    abstract fun knowledgeDao(): KnowledgeDao

    companion object {
        @Volatile
        private var INSTANCE: KayaDatabase? = null

        fun getDatabase(context: Context): KayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KayaDatabase::class.java,
                    "kaya_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: KayaDatabase) {
                // Pre-populate sample construction site hazards
                db.hazardDao().insertHazard(
                    HazardEntity(
                        title = "Worker missing High-Vis Vest & Glasses",
                        category = "PPE",
                        severity = "HIGH",
                        location = "Grid B-4 (Floor 3 Deck)",
                        description = "Subcontractor observed operating circular saw without safety goggles and high-visibility vest."
                    )
                )
                db.hazardDao().insertHazard(
                    HazardEntity(
                        title = "Unprotected Edge / Fall Hazard",
                        category = "Fall Risk",
                        severity = "CRITICAL",
                        location = "East Elevator Shaft - Level 4",
                        description = "Guardrail missing toe-board and mid-rail section near east opening. Tie-off required."
                    )
                )
                db.hazardDao().insertHazard(
                    HazardEntity(
                        title = "Tower Crane Proximity Warning",
                        category = "Crane Proximity",
                        severity = "MEDIUM",
                        location = "Zone 2 Overhead",
                        description = "Slew radius intersects suspended load path over active rebar placement crew."
                    )
                )
                db.hazardDao().insertHazard(
                    HazardEntity(
                        title = "Exposed Temporary Power Junction",
                        category = "Electrical",
                        severity = "HIGH",
                        location = "Basement MEP Shaft",
                        description = "Water accumulation near open 480V temporary distribution box."
                    )
                )

                // Pre-populate Blueprints
                db.blueprintDao().insertBlueprint(
                    BlueprintEntity(code = "A-101", title = "Ground Floor Architectural Plan", type = "ARCHITECTURAL", revision = "Rev 3", deviationCount = 0)
                )
                db.blueprintDao().insertBlueprint(
                    BlueprintEntity(code = "S-204", title = "Level 3 Structural Beam Layout", type = "STRUCTURAL", revision = "Rev 5", deviationCount = 2)
                )
                db.blueprintDao().insertBlueprint(
                    BlueprintEntity(code = "MEP-302", title = "HVAC & Electrical Conduit Riser", type = "MEP", revision = "Rev 2", deviationCount = 1)
                )
                db.blueprintDao().insertBlueprint(
                    BlueprintEntity(code = "GFC-108", title = "Final Execution Foundation Blueprint", type = "GFC", revision = "GFC Final", deviationCount = 0)
                )

                // Pre-populate Reports
                db.reportDao().insertReport(
                    ReportEntity(type = "DPR", title = "Daily Progress Log - Block A Level 3", summary = "Rebar layout 90% completed. Concrete pour scheduled for 08:00 tomorrow.", crewCount = 22, hazardsFound = 2)
                )
                db.reportDao().insertReport(
                    ReportEntity(type = "SAFETY", title = "OSHA Safety Audit #42", summary = "100% hardhat compliance. 1 non-conformance flag logged for scaffolding toe-board.", crewCount = 35, hazardsFound = 1)
                )
                db.reportDao().insertReport(
                    ReportEntity(type = "NCR", title = "NCR-014: Concrete Beam Alignment Defect", summary = "Post-pour ultrasonic test revealed 14mm variance on Beam B-12.", crewCount = 6, hazardsFound = 1)
                )

                // Pre-populate Knowledge RAG Base
                db.knowledgeDao().insertKnowledge(
                    KnowledgeItemEntity(title = "OSHA 1926.501 - Fall Protection Standard", category = "SAFETY_MANUAL", contentSnippet = "Unprotected sides and edges 6 feet or more above lower levels shall be protected by guardrail systems, safety net systems, or personal fall arrest systems.", tags = "OSHA, Fall, Safety, Harness")
                )
                db.knowledgeDao().insertKnowledge(
                    KnowledgeItemEntity(title = "Structural Steel Beam Installation SOP (W14x90)", category = "SOP", contentSnippet = "Step 1: Check anchor bolt alignment tolerance (+/- 3mm). Step 2: Torque Grade 8.8 bolts to 350 Nm using calibrated wrench.", tags = "Steel, Beam, Installation, Torque")
                )
                db.knowledgeDao().insertKnowledge(
                    KnowledgeItemEntity(title = "Concrete Slump & Strength MTC Verification", category = "BOQ", contentSnippet = "C35/45 grade concrete requires 28-day characteristic strength of 45 MPa. Slump range: 120mm - 150mm.", tags = "Concrete, MTC, Cement, Slump")
                )
                db.knowledgeDao().insertKnowledge(
                    KnowledgeItemEntity(title = "Scaffolding Load Limits & Tie-off Rule", category = "CODE", contentSnippet = "Heavy-duty scaffolds max load 75 lbs/sq ft. Vertical ties required every 26 feet max for scaffolds wider than 3 feet.", tags = "Scaffold, Load, Safety")
                )
            }
        }
    }
}
