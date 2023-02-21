package com.example.mukfinalproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mukfinalproject.R
import com.example.mukfinalproject.adapter.MukChooserAdapter
import kotlinx.android.synthetic.main.activity_muk_chooser.*
import java.util.*

class MukChooserActivity : AppCompatActivity(), MukChooserAdapter.MukChooserAdapterListener {

    // Variables
    private lateinit var mukChoices: List<String>
    private lateinit var mukChooserAdapter: MukChooserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muk_chooser)

        mukSetupToolbar()
        mukGetIntentData()
        mukSetupAdapter()
    }

    // MukChooserAdapter.MukChooserAdapterListener Methods
    override fun mukOnChooseItem(mukChoice: String) {
        val mukIntent = Intent()
        mukIntent.putExtra(MUK_CHOSEN_EXTRA, mukChoice)
        setResult(RESULT_OK, mukIntent)

        finish()
    }

    // Utilities
    private fun mukSetupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun mukGetIntentData() {
        if (intent.hasExtra(MukProfileDetailsActivity.MUK_GENDER_EXTRA)) {
            mukChoices = mukGetGenderChoices()
            title = "Choose Gender"
        } else if (intent.hasExtra(MukProfileDetailsActivity.MUK_COUNTRY_EXTRA)) {
            mukChoices = mukGetCountryChoices()
            title = "Choose Country"
        }
    }

    private fun mukGetGenderChoices(): List<String> {
        return listOf("Male", "Female", "Other")
    }

    private fun mukGetCountryChoices(): List<String> {
        var mukCountries = ArrayList<String>()

        val mukIsoCountryCodes = Locale.getISOCountries()
        for (mukCountryCode in mukIsoCountryCodes) {
            val mukLocale = Locale("", mukCountryCode)
            mukCountries.add(mukLocale.displayCountry)
        }

        return mukCountries
    }

    private fun mukSetupAdapter() {
        val mukLayoutManager = LinearLayoutManager(this)
        mukChooserRecyclerView.layoutManager = mukLayoutManager

        mukChooserAdapter = MukChooserAdapter(mukChoices, this)
        mukChooserRecyclerView.adapter = mukChooserAdapter
    }

    companion object {
        const val MUK_CHOSEN_EXTRA = "MUK_CHOSEN_EXTRA"
    }

}