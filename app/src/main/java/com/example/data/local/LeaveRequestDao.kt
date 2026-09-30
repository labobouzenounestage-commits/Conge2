package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveRequestDao {
    @Query("SELECT * FROM leave_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRequestsByUser(userId: Long): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE substituteId = :substituteId ORDER BY timestamp DESC")
    fun getRequestsBySubstitute(substituteId: Long): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE substituteId = :substituteId AND status = 'PENDING_SUBSTITUTE' ORDER BY timestamp DESC")
    fun getPendingSubstituteRequests(substituteId: Long): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE status = 'PENDING_MANAGER' ORDER BY timestamp DESC")
    fun getPendingManagerRequests(): Flow<List<LeaveRequestEntity>>

    @Query("SELECT * FROM leave_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: Long): LeaveRequestEntity?

    @Query("SELECT COUNT(*) FROM leave_requests")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: LeaveRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<LeaveRequestEntity>)

    @Update
    suspend fun updateRequest(request: LeaveRequestEntity)

    @Query("UPDATE leave_requests SET status = :status WHERE id = :requestId")
    suspend fun updateStatus(requestId: Long, status: LeaveStatus)

    @Delete
    suspend fun deleteRequest(request: LeaveRequestEntity)

    @Query("DELETE FROM leave_requests")
    suspend fun deleteAllRequests()
}
