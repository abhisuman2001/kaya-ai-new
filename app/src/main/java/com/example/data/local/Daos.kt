package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HazardDao {
    @Query("SELECT * FROM hazards ORDER BY timestamp DESC")
    fun getAllHazards(): Flow<List<HazardEntity>>

    @Query("SELECT * FROM hazards WHERE isResolved = 0 ORDER BY timestamp DESC")
    fun getActiveHazards(): Flow<List<HazardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHazard(hazard: HazardEntity): Long

    @Update
    suspend fun updateHazard(hazard: HazardEntity)

    @Query("DELETE FROM hazards WHERE id = :id")
    suspend fun deleteHazard(id: Int)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long
}

@Dao
interface BlueprintDao {
    @Query("SELECT * FROM blueprints ORDER BY code ASC")
    fun getAllBlueprints(): Flow<List<BlueprintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprint(blueprint: BlueprintEntity)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge ORDER BY id ASC")
    fun getAllKnowledge(): Flow<List<KnowledgeItemEntity>>

    @Query("SELECT * FROM knowledge WHERE title LIKE '%' || :query || '%' OR contentSnippet LIKE '%' || :query || '%'")
    fun searchKnowledge(query: String): Flow<List<KnowledgeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledge(knowledge: KnowledgeItemEntity)
}
