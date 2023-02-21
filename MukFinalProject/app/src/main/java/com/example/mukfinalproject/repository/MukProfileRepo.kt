package com.example.mukfinalproject.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mukfinalproject.db.MukFinalProjectDatabase
import com.example.mukfinalproject.model.MukProfile

class MukProfileRepo(var context: Context) {

    // Variables
    private var mukDb = MukFinalProjectDatabase.mukGetInstance(context)
    private var mukProfileDao = mukDb.mukProfileDao()

    // Methods
    fun mukGetAllLiveProfiles(): LiveData<List<MukProfile>> {
        return mukProfileDao.mukLoadAll()
    }

    fun mukGetLiveProfile(mukProfileId: Long): LiveData<MukProfile> {
        return mukProfileDao.mukLoadLiveProfile(mukProfileId)
    }

    fun mukGetProfile(mukProfileId: Long): MukProfile {
        return mukProfileDao.mukLoadProfile(mukProfileId)
    }

    fun mukAddProfile(mukProfile: MukProfile): Long {
        val mukId = mukProfileDao.mukInsertProfile(mukProfile)
        mukProfile.mukId = mukId

        return mukId
    }

    fun mukUpdateProfile(mukProfile: MukProfile) {
        mukProfileDao.mukUpdateProfile(mukProfile)
    }

    fun mukDeleteProfile(mukProfile: MukProfile) {
        mukProfile.mukDeleteImage(context)
        mukProfileDao.mukDeleteProfile(mukProfile)
    }

    fun mukCreateProfile(): MukProfile {
        return MukProfile()
    }

}