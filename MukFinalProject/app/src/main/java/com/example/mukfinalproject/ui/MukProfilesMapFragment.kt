package com.example.mukfinalproject.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.mukfinalproject.R
import com.example.mukfinalproject.viewmodel.MukSharedProfilesViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.max
import kotlin.math.min

class MukProfilesMapFragment(
    private var mukMapFragmentListener: MukMapFragmentListener?
): Fragment() {

    // Interface
    interface MukMapFragmentListener {
        fun mukOnMapProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView)
    }

    // Variables
    private lateinit var mukSharedProfilesViewModel: MukSharedProfilesViewModel
    private var mukFixedProfileViews: List<MukSharedProfilesViewModel.MukProfileView> = listOf()
    private var mukBufferProfileViews: MutableList<MukSharedProfilesViewModel.MukProfileView> = mutableListOf()
    private lateinit var mukMap: GoogleMap
    private val callback = OnMapReadyCallback { googleMap ->
        mukMap = googleMap
        mukSetupViewModel()
        mukSetupProfilesObserver()
        mukSetupMapListeners()
    }

    // Fragment Lifecycle Methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_muk_profiles_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    // Methods
    fun mukSearch(mukQuery: String) {
        if (view != null) {
            if (mukQuery.isEmpty()) {
                mukBufferProfileViews = mukFixedProfileViews.toMutableList()
            } else {
                val mukFiltered = mukFixedProfileViews.filter {
                    it.mukName.contains(mukQuery)
                }

                mukBufferProfileViews = mukFiltered.toMutableList()
            }

            mukUpdateUI()
        }
    }

    // Utilities
    private fun mukSetupViewModel() {
        mukSharedProfilesViewModel = ViewModelProvider(requireActivity()).get(MukSharedProfilesViewModel::class.java)
    }

    private fun mukSetupProfilesObserver() {
        mukSharedProfilesViewModel.mukGetProfileViews()?.observe(viewLifecycleOwner) {
            it?.let {
                mukFixedProfileViews = it
                mukBufferProfileViews = it.toMutableList()

                mukUpdateUI()
            }
        }
    }

    private fun mukUpdateUI() {
        mukAddMarkers(mukBufferProfileViews)
        mukUpdateCamera(mukBufferProfileViews)
    }

    private fun mukAddMarkers(mukProfileViews: List<MukSharedProfilesViewModel.MukProfileView>) {
        mukMap.clear()

        for (mukProfileView in mukProfileViews) {
            mukAddMarker(mukProfileView)
        }
    }

    private fun mukAddMarker(mukProfileView: MukSharedProfilesViewModel.MukProfileView) {
        val mukLatLng = LatLng(mukProfileView.mukLatitude, mukProfileView.mukLongitude)
        val mukSnippetString = "${mukProfileView.mukGender}" +
                " in ${mukProfileView.mukCountry}" +
                " born on ${mukProfileView.mukBirthday}"

        val mukMarker = mukMap.addMarker(MarkerOptions()
            .position(mukLatLng)
            .title(mukProfileView.mukName)
            .snippet(mukSnippetString))

        mukMarker?.tag = mukProfileView
    }

    private fun mukUpdateCamera(mukProfileViews: List<MukSharedProfilesViewModel.MukProfileView>) {
        if (mukProfileViews.isEmpty()) {
            return
        }

        var mukSouth = 90.0
        var mukWest = 180.0
        var mukNorth = -90.0
        var mukEast = -180.0

        for (mukPlaceView in mukProfileViews) {
            mukSouth = min(mukSouth, mukPlaceView.mukLatitude)
            mukWest = min(mukWest, mukPlaceView.mukLongitude)
            mukNorth = max(mukNorth, mukPlaceView.mukLatitude)
            mukEast = max(mukEast, mukPlaceView.mukLongitude)
        }

        val mukSouthWest = LatLng(mukSouth, mukWest)
        val mukNorthEast = LatLng(mukNorth, mukEast)
        val mukBounds = LatLngBounds(mukSouthWest, mukNorthEast)

        val mukCameraUpdate = CameraUpdateFactory.newLatLngBounds(mukBounds, 50)
        mukMap.animateCamera(mukCameraUpdate)
    }

    private fun mukSetupMapListeners() {
        mukMap.setOnInfoWindowClickListener {
            val mukProfileView = it.tag as? MukSharedProfilesViewModel.MukProfileView
            mukProfileView?.let { mukProfileView ->
                mukMapFragmentListener?.mukOnMapProfileClicked(mukProfileView)
            }
        }
    }

    companion object {
        fun mukNewInstance(mukMapFragmentListener: MukMapFragmentListener? = null): MukProfilesMapFragment {
            return MukProfilesMapFragment(mukMapFragmentListener)
        }
    }
}