package com.example.mukfinalproject.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.mukfinalproject.model.MukProfile
import com.example.mukfinalproject.repository.MukProfileRepo
import com.example.mukfinalproject.util.MukDateUtils
import com.example.mukfinalproject.util.MukImageUtils
import java.util.*

class MukSharedProfilesViewModel(application: Application): AndroidViewModel(application) {

    // Variables
    private var mukProfileRepo = MukProfileRepo(getApplication())
    private var mukProfileViews: LiveData<List<MukProfileView>>? = null

    // Methods
    fun mukGetProfileViews(): LiveData<List<MukProfileView>>? {
        if (mukProfileViews == null) {
            mukMapProfilesToProfileViews()
        }

        return mukProfileViews
    }

    // Utilities
    private fun mukProfileToProfileView(mukProfile: MukProfile): MukProfileView {
        return MukProfileView(
            mukProfile.mukId,
            mukProfile.mukName,
            mukProfile.mukGender,
            mukProfile.mukCountry,
            mukProfile.mukLatitude,
            mukProfile.mukLongitude,
            MukDateUtils.mukDateToString(mukProfile.mukBirthday)
        )
    }

    private fun mukMapProfilesToProfileViews() {
        mukProfileViews = Transformations.map(mukProfileRepo.mukGetAllLiveProfiles()) { mukRepoProfiles ->
            mukRepoProfiles.map { mukProfile ->
                mukProfileToProfileView(mukProfile)
            }
        }
    }

    data class MukProfileView(
        var mukId: Long? = null,
        var mukName: String = "",
        var mukGender: String = "",
        var mukCountry: String = "",
        var mukLatitude: Double = 0.0,
        var mukLongitude: Double = 0.0,
        var mukBirthday: String = ""
    ) {
        fun mukGetProfileImage(mukContext: Context): Bitmap? {
            mukId?.let {
                return MukImageUtils.mukLoadBitmapFromFile(mukContext, MukProfile.mukImageFileName(it))
            }

            return null
        }
    }

}