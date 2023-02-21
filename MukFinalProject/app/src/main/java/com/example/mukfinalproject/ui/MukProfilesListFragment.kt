package com.example.mukfinalproject.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mukfinalproject.R
import com.example.mukfinalproject.adapter.MukProfileListAdapter
import com.example.mukfinalproject.viewmodel.MukSharedProfilesViewModel
import kotlinx.android.synthetic.main.fragment_muk_profiles_list.*

class MukProfilesListFragment(
    private var mukListFragmentListener: MukListFragmentListener?
): Fragment(), MukProfileListAdapter.MukProfileListAdapterListener {

    // Interface
    interface MukListFragmentListener {
        fun mukOnListProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView)
    }

    // Variables
    private lateinit var mukSharedProfilesViewModel: MukSharedProfilesViewModel
    private var mukFixedProfileViews: List<MukSharedProfilesViewModel.MukProfileView> = listOf()
    private var mukBufferProfileViews: MutableList<MukSharedProfilesViewModel.MukProfileView> = mutableListOf()
    private lateinit var mukProfileListAdapter: MukProfileListAdapter

    // Fragment Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_muk_profiles_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mukSetupViewModels()
        mukSetupAdapter()
        mukSetupProfilesObserver()
    }

    // MukProfileListAdapter.MukProfileListAdapterListener Methods
    override fun mukOnProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView) {
        mukListFragmentListener?.mukOnListProfileClicked(mukProfileView)
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
    private fun mukSetupViewModels() {
        mukSharedProfilesViewModel = ViewModelProvider(requireActivity()).get(MukSharedProfilesViewModel::class.java)
    }

    private fun mukSetupAdapter() {
        val mukLayoutManager = LinearLayoutManager(context)
        mukRecyclerView.layoutManager = mukLayoutManager

        mukProfileListAdapter = MukProfileListAdapter(null, this, context)
        mukRecyclerView.adapter = mukProfileListAdapter
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
        mukProfileListAdapter.mukSetProfileViews(mukBufferProfileViews)
    }

    companion object {
        fun mukNewInstance(mukListFragmentListener: MukListFragmentListener? = null): MukProfilesListFragment {
            return MukProfilesListFragment(mukListFragmentListener)
        }
    }

}