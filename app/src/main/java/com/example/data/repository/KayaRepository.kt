package com.example.data.repository

import com.example.data.local.BlueprintDao
import com.example.data.local.BlueprintEntity
import com.example.data.local.HazardDao
import com.example.data.local.HazardEntity
import com.example.data.local.KnowledgeDao
import com.example.data.local.KnowledgeItemEntity
import com.example.data.local.ReportDao
import com.example.data.local.ReportEntity
import kotlinx.coroutines.flow.Flow

class KayaRepository(
    private val hazardDao: HazardDao,
    private val reportDao: ReportDao,
    private val blueprintDao: BlueprintDao,
    private val knowledgeDao: KnowledgeDao
) {
    val allHazards: Flow<List<HazardEntity>> = hazardDao.getAllHazards()
    val activeHazards: Flow<List<HazardEntity>> = hazardDao.getActiveHazards()
    val allReports: Flow<List<ReportEntity>> = reportDao.getAllReports()
    val allBlueprints: Flow<List<BlueprintEntity>> = blueprintDao.getAllBlueprints()
    val allKnowledge: Flow<List<KnowledgeItemEntity>> = knowledgeDao.getAllKnowledge()

    suspend fun insertHazard(hazard: HazardEntity): Long = hazardDao.insertHazard(hazard)
    suspend fun updateHazard(hazard: HazardEntity) = hazardDao.updateHazard(hazard)
    suspend fun resolveHazard(id: Int, actionTaken: String) {
        // Find and update hazard
        // For simplicity, resolve via SQL or direct update call
    }

    suspend fun insertReport(report: ReportEntity): Long = reportDao.insertReport(report)
    suspend fun insertBlueprint(blueprint: BlueprintEntity) = blueprintDao.insertBlueprint(blueprint)
    fun searchKnowledge(query: String): Flow<List<KnowledgeItemEntity>> = knowledgeDao.searchKnowledge(query)
}
