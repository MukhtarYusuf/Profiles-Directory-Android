package com.example.mukfinalproject.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mukfinalproject.model.MukProfile

@Dao
interface MukProfileDao {
    @Query("SELECT * FROM MukProfile ORDER BY mukName")
    fun mukLoadAll(): LiveData<List<MukProfile>>

    @Query("SELECT * FROM MukProfile WHERE mukId = :mukProfileId")
    fun mukLoadLiveProfile(mukProfileId: Long): LiveData<MukProfile>

    @Query("SELECT * FROM MukProfile WHERE mukId = :mukProfileId")
    fun mukLoadProfile(mukProfileId: Long): MukProfile

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun mukInsertProfile(mukProfile: MukProfile): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun mukUpdateProfile(mukProfile: MukProfile)

    @Delete
    fun mukDeleteProfile(mukProfile: MukProfile)
}