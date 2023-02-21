package com.example.mukfinalproject.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import com.example.mukfinalproject.R
import com.example.mukfinalproject.viewmodel.MukSharedProfilesViewModel
import kotlinx.android.synthetic.main.activity_muk_profiles.*
import kotlinx.android.synthetic.main.muk_drawer_view_profiles.*
import kotlinx.android.synthetic.main.muk_profiles_main_content.*

class MukProfilesActivity: AppCompatActivity(),
    MukProfilesListFragment.MukListFragmentListener,
    MukProfilesMapFragment.MukMapFragmentListener {

    // Variables
    private var mukListFragment: MukProfilesListFragment? = null
    private var mukMapFragment: MukProfilesMapFragment? = null

    // Activity Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muk_profiles)

        mukSetupToolbar()
        mukSetupFragments()
        mukSetupListeners()
        mukDisplayList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profiles, menu)

        val mukSearchMenuItem = menu.findItem(R.id.search_item)
        val mukSearchView = mukSearchMenuItem?.actionView as SearchView

        val mukSearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mukSearchView.setSearchableInfo(mukSearchManager.getSearchableInfo(componentName))

        mukSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val mukNewText = newText ?: ""

                mukListFragment?.mukSearch(mukNewText)
                mukMapFragment?.mukSearch(mukNewText)

                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.mukAddItem) {
//            mukGoToProfileDetails(null)
//        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        mukHandleSearchIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Cleanup Fragments?
//        mukListFragment = null
//        mukMapFragment = null
    }

    // MukProfilesListFragment.MukListFragmentListener Methods
    override fun mukOnListProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView) {
        mukProfileView.mukId?.let {
            mukGoToProfileDetails(it)
        }
    }

    // MukProfilesMapFragment.MukMapFragmentListener Methods
    override fun mukOnMapProfileClicked(mukProfileView: MukSharedProfilesViewModel.MukProfileView) {
        mukProfileView.mukId?.let {
            mukGoToProfileDetails(it)
        }
    }

    // Utilities
    private fun mukSetupToolbar() {
        setSupportActionBar(toolbar)
        val mukToggle = ActionBarDrawerToggle(this,
            mukDrawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer)

        mukToggle.syncState()
    }

    private fun mukSetupFragments() {
        mukListFragment = MukProfilesListFragment.mukNewInstance(this)
        mukMapFragment = MukProfilesMapFragment.mukNewInstance(this)
    }

    private fun mukSetupListeners() {
        mukListButton.setOnClickListener {
            mukDisplayList()
            mukDrawerLayout.closeDrawer(mukDrawerView)
        }
        mukMapButton.setOnClickListener {
            mukDisplayMap()
            mukDrawerLayout.closeDrawer(mukDrawerView)
        }

        mukFab.setOnClickListener {
            mukGoToProfileDetails(null)
        }
    }

    private fun mukHandleSearchIntent(mukIntent: Intent) {
        if (mukIntent.action == Intent.ACTION_SEARCH) {
            val mukQuery = mukIntent.getStringExtra(SearchManager.QUERY)

            mukQuery?.let {
                mukListFragment?.mukSearch(it)
                mukMapFragment?.mukSearch(it)
            }
        }
    }

    private fun mukDisplayList() {
        mukListFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mukProfilesContent, it)
                .commit()
        }
    }

    private fun mukDisplayMap() {
        mukMapFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mukProfilesContent, it)
                .commit()
        }
    }

    private fun mukGoToProfileDetails(mukId: Long?) {
        val mukIntent = Intent(this, MukProfileDetailsActivity::class.java)

        mukId?.let {
            mukIntent.putExtra(MUK_PROFILE_ID, mukId)
        }

        startActivity(mukIntent)
    }

    companion object {
        const val MUK_PROFILE_ID = "MUK_PROFILE_ID"
    }
}