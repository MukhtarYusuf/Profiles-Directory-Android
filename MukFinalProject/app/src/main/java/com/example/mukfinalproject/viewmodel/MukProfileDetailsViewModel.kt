package com.example.mukfinalproject.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.mukfinalproject.model.MukProfile
import com.example.mukfinalproject.repository.MukProfileRepo
import com.example.mukfinalproject.util.MukImageUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MukProfileDetailsViewModel(application: Application): AndroidViewModel(application) {

    // Variables
    private var mukProfileRepo = MukProfileRepo(getApplication())
    private var mukProfileDetailsView: LiveData<MukProfileDetailsView>? = null

    // Methods
    fun mukGetProfileView(mukId: Long): LiveData<MukProfileDetailsView>? {
        if (mukProfileDetailsView == null) {
            mukMapProfileToProfileDetailsView(mukId)
        }

        return mukProfileDetailsView
    }

    fun mukAddProfile(mukProfileDetailsView: MukProfileDetailsView) {
        val mukProfile = mukProfileRepo.mukCreateProfile()
        mukProfile.mukName = mukProfileDetailsView.mukName
        mukProfile.mukLatitude = mukProfileDetailsView.mukLatitude.toDouble()
        mukProfile.mukLongitude = mukProfileDetailsView.mukLongitude.toDouble()
        mukProfile.mukGender = mukProfileDetailsView.mukGender
        mukProfile.mukCountry = mukProfileDetailsView.mukCountry
        mukProfile.mukBirthday = mukProfileDetailsView.mukBirthday

        GlobalScope.launch {
            mukProfileRepo.mukAddProfile(mukProfile)
        }
    }

    fun mukUpdateProfile(mukProfileDetailsView: MukProfileDetailsView) {
        GlobalScope.launch {
            val mukProfile = mukProfileDetailsViewToProfile(mukProfileDetailsView)
            mukProfile?.let {
                mukProfileRepo.mukUpdateProfile(it)
            }
        }
    }

    fun mukDeleteProfile(mukProfileDetailsView: MukProfileDetailsView) {
        GlobalScope.launch {
            val mukProfile = mukProfileDetailsViewToProfile(mukProfileDetailsView)
            mukProfile?.let {
                mukProfileRepo.mukDeleteProfile(it)
            }
        }
    }

    fun mukCreateProfileDetailsView(): MukProfileDetailsView {
        return MukProfileDetailsView()
    }

    // Utilities
    private fun mukProfileToProfileDetailsView(mukProfile: MukProfile): MukProfileDetailsView {
        return MukProfileDetailsView(
            mukProfile.mukId,
            mukProfile.mukName,
            mukProfile.mukGender,
            mukProfile.mukCountry,
            "${mukProfile.mukLatitude}",
            "${mukProfile.mukLongitude}",
            mukProfile.mukBirthday
        )
    }

    private fun mukProfileDetailsViewToProfile(mukProfileDetailsView: MukProfileDetailsView): MukProfile? {
        val mukProfile = mukProfileDetailsView.mukId?.let {
            mukProfileRepo.mukGetProfile(it)
        }

        mukProfile?.let {
            it.mukId = mukProfileDetailsView.mukId
            it.mukName = mukProfileDetailsView.mukName
            it.mukLatitude = mukProfileDetailsView.mukLatitude.toDouble()
            it.mukLongitude = mukProfileDetailsView.mukLongitude.toDouble()
            it.mukGender = mukProfileDetailsView.mukGender
            it.mukCountry = mukProfileDetailsView.mukCountry
            it.mukBirthday = mukProfileDetailsView.mukBirthday
        }

        return mukProfile
    }

    private fun mukMapProfileToProfileDetailsView(mukId: Long) {
        val mukProfile = mukProfileRepo.mukGetLiveProfile(mukId)
        mukProfileDetailsView = Transformations.map(mukProfile) {
            it?.let {
                mukProfileToProfileDetailsView(it)
            }
        }
    }

    data class MukProfileDetailsView(
        var mukId: Long? = null,
        var mukName: String = "",
        var mukGender: String = "",
        var mukCountry: String = "",
        var mukLatitude: String = "",
        var mukLongitude: String = "",
        var mukBirthday: Date = Date()
    ) {
        fun mukGetProfileImage(mukContext: Context): Bitmap? {
            mukId?.let {
                return MukImageUtils.mukLoadBitmapFromFile(mukContext, MukProfile.mukImageFileName(it))
            }

            return null
        }

        fun mukSetProfileImage(mukContext: Context, mukImage: Bitmap) {
            mukId?.let {
                MukImageUtils.mukSaveBitmapToFile(mukContext, mukImage, MukProfile.mukImageFileName(it))
            }
        }
    }

}