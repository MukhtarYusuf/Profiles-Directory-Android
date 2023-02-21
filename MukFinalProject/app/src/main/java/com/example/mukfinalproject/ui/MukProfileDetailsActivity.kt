package com.example.mukfinalproject.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.mukfinalproject.R
import com.example.mukfinalproject.util.MukImageUtils
import com.example.mukfinalproject.viewmodel.MukProfileDetailsViewModel
import kotlinx.android.synthetic.main.activity_muk_profile_details.*
import java.io.File
import java.util.*

class MukProfileDetailsActivity : AppCompatActivity(), MukPhotoSourceDialogFragment.PhotoOptionDialogListener {

    // Variables
    private var mukIsAdd = true
    private lateinit var mukProfileDetailsViewModel: MukProfileDetailsViewModel
    private var mukProfileDetailsView: MukProfileDetailsViewModel.MukProfileDetailsView? = null
    private var mukDeleteItem: MenuItem? = null
    private var mukPhotoFile: File? = null
    private var mukImageBitmap: Bitmap? = null

    // Activity Lifecycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muk_profile_details)

        mukSetupToolbar()
        mukSetupViewModel()
        mukSetupProfileObserver()
        mukSetupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile_details, menu)
        mukDeleteItem = menu?.findItem(R.id.mukDeleteItem)
        mukUpdateUI()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mukSaveItem -> {
                mukSaveProfile()
            }
            R.id.mukDeleteItem -> {
                mukDeleteProfile()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                MUK_REQUEST_CAPTURE_IMAGE -> {
                    val mukPhotoFile = mukPhotoFile ?: return
                    val mukUri = FileProvider.getUriForFile(this,
                        "com.example.mukfinalproject.fileprovider",
                        mukPhotoFile)
                    revokeUriPermission(mukUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    mukImageBitmap = mukGetImageFromPath(mukPhotoFile.absolutePath)
                    mukUpdateImageView()
                }
                MUK_REQUEST_GALLERY_IMAGE -> {
                    val mukImageUri = data?.data
                    mukImageUri?.let { mukUri ->
                        mukImageBitmap = mukGetImageFromUri(mukUri)
                        mukUpdateImageView()
                    }
                }
                MUK_REQUEST_GENDER -> {
                    val mukIntent = data ?: return

                    val mukGender = mukIntent.getStringExtra(MukChooserActivity.MUK_CHOSEN_EXTRA)
                    mukGender?.let {
                        mukGenderTextView.text = it
                    }
                }
                MUK_REQUEST_COUNTRY -> {
                    val mukIntent = data ?: return

                    val mukCountry = mukIntent.getStringExtra(MukChooserActivity.MUK_CHOSEN_EXTRA)
                    mukCountry?.let {
                        mukCountryTextView.text = it
                    }
                }
            }
        }
    }

    // MukPhotoSourceDialogFragment.PhotoOptionDialogListener Methods
    override fun onCaptureClick() {
        mukPhotoFile = null
        try {
            mukPhotoFile = MukImageUtils.mukCreateUniqueImageFile(this)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            return
        }

        mukPhotoFile?.let { mukPhotoFile ->
            val mukPhotoUri = FileProvider.getUriForFile(this, "com.example.mukfinalproject.fileprovider", mukPhotoFile)

            val mukCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            mukCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mukPhotoUri)

            // Resolve Activities and Grant Permissions
            val mukIntentActivities = packageManager.queryIntentActivities(mukCaptureIntent, PackageManager.MATCH_DEFAULT_ONLY)
            mukIntentActivities.map {
                it.activityInfo.packageName
            }.forEach{
                grantUriPermission(it, mukPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            startActivityForResult(mukCaptureIntent, MUK_REQUEST_CAPTURE_IMAGE)
        }
    }

    override fun onPickClick() {
        val mukPickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(mukPickIntent, MUK_REQUEST_GALLERY_IMAGE)
    }

    // Utilities
    private fun mukSetupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun mukSetupViewModel() {
        mukProfileDetailsViewModel = ViewModelProvider(this).get(MukProfileDetailsViewModel::class.java)
    }

    private fun mukSetupProfileObserver() {
        val mukId = intent.getLongExtra(MukProfilesActivity.MUK_PROFILE_ID, 0)
        mukProfileDetailsViewModel.mukGetProfileView(mukId)?.observe(this) {
            it?.let {
                mukIsAdd = false
                mukProfileDetailsView = it
                mukUpdateUI()
            }
        }
    }

    private fun mukSetupListeners() {
        mukProfileImageView.setOnClickListener {
            mukDisplayPhotoSourceDialog()
        }
        mukGenderLayout.setOnClickListener {
            mukChooseGender()
        }
        mukCountryLayout.setOnClickListener {
            mukChooseCountry()
        }
    }

    private fun mukSaveProfile() {
        if (mukProfileDetailsView == null) {
            mukProfileDetailsView = mukProfileDetailsViewModel.mukCreateProfileDetailsView()
        }

        mukProfileDetailsView?.let {
            if (mukValidateFields()) {
                mukImageBitmap?.let { mukImageBitmap ->
                    it.mukSetProfileImage(this, mukImageBitmap)
                }

                it.mukName = mukNameEditText.text.toString()
                it.mukLatitude = mukLatitudeEditText.text.toString()
                it.mukLongitude = mukLongitudeEditText.text.toString()
                it.mukGender = mukGenderTextView.text.toString()
                it.mukCountry = mukCountryTextView.text.toString()

                val mukCalendar: Calendar = Calendar.getInstance()
                mukCalendar.set(Calendar.YEAR, mukDatePicker.year)
                mukCalendar.set(Calendar.MONTH, mukDatePicker.month)
                mukCalendar.set(Calendar.DAY_OF_MONTH, mukDatePicker.dayOfMonth)
                mukCalendar.set(Calendar.HOUR_OF_DAY, 0)
                mukCalendar.set(Calendar.MINUTE, 0)
                mukCalendar.set(Calendar.SECOND, 0)
                mukCalendar.set(Calendar.MILLISECOND, 0)

                it.mukBirthday = mukCalendar.time

                var mukMessage = ""
                if (mukIsAdd) {
                    mukProfileDetailsViewModel.mukAddProfile(it)
                    mukMessage = "Profile Added"
                } else {
                    mukProfileDetailsViewModel.mukUpdateProfile(it)
                    mukMessage = "Profile Updated"
                }

                Toast.makeText(this, mukMessage, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun mukDeleteProfile() {
        mukProfileDetailsView?.let {
            mukProfileDetailsViewModel.mukDeleteProfile(it)
            Toast.makeText(this, "Profile Deleted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun mukUpdateImageView() {
        mukImageBitmap?.let {
            mukProfileImageView.setImageBitmap(it)
        }
    }

    private fun mukUpdateUI() {
        mukDeleteItem?.isVisible = !mukIsAdd

        mukProfileDetailsView?.let {
            mukImageBitmap = it.mukGetProfileImage(this)
            mukUpdateImageView()

            mukNameEditText.setText(it.mukName)
            mukLatitudeEditText.setText(it.mukLatitude)
            mukLongitudeEditText.setText(it.mukLongitude)
            mukGenderTextView.text = it.mukGender
            mukCountryTextView.text = it.mukCountry

            val mukCalendar = Calendar.getInstance()
            mukCalendar.time = it.mukBirthday

            mukDatePicker.updateDate(mukCalendar.get(Calendar.YEAR),
                mukCalendar.get(Calendar.MONTH), mukCalendar.get(Calendar.DAY_OF_MONTH))
        }
    }

    private fun mukValidateFields(): Boolean {
        var mukIsValid = true
        var mukMessage = ""

        val mukValidName = mukNameEditText.text.toString()
        if (mukValidName.isEmpty()) {
            mukIsValid = false
            mukMessage = "Please Enter a Valid Name \n"
        }

        try {
            val mukLatitude = mukLatitudeEditText.text.toString().toDouble()
        } catch (e: Exception) {
            mukIsValid = false
            mukMessage += "Please Enter a Valid Latitude \n"
        }

        try {
            val mukLongitude = mukLongitudeEditText.text.toString().toDouble()
        } catch (e: Exception) {
            mukIsValid = false
            mukMessage += "Please Enter a Valid Longitude \n"
        }

        if (!mukIsValid) {
            mukDisplayAlert(mukMessage)
        }

        return mukIsValid
    }

    private fun mukDisplayAlert(mukMessage: String) {
        AlertDialog.Builder(this)
            .setTitle("Invalid Input")
            .setMessage(mukMessage)
            .setPositiveButton("Ok", null)
            .create()
            .show()
    }

    private fun mukDisplayPhotoSourceDialog() {
        val mukFragment = MukPhotoSourceDialogFragment.newInstance(this)
        mukFragment?.show(supportFragmentManager, "mukPhotoSourceDialogFragment")
    }

    private fun mukGetImageFromPath(mukFilePath: String): Bitmap? {
        return MukImageUtils.mukDecodeFileToSize(mukFilePath,
            R.dimen.default_image_width,
            R.dimen.default_image_height)
    }

    private fun mukGetImageFromUri(mukUri: Uri): Bitmap? {
        return MukImageUtils.mukDecodeUriStreamToSize(mukUri,
            R.dimen.default_image_width,
            R.dimen.default_image_height, this)
    }

    private fun mukChooseGender() {
        val mukIntent = Intent(this, MukChooserActivity::class.java)
        mukIntent.putExtra(MUK_GENDER_EXTRA, "")

        startActivityForResult(mukIntent, MUK_REQUEST_GENDER)
    }

    private fun mukChooseCountry() {
        val mukIntent = Intent(this, MukChooserActivity::class.java)
        mukIntent.putExtra(MUK_COUNTRY_EXTRA, "")

        startActivityForResult(mukIntent, MUK_REQUEST_COUNTRY)
    }

    companion object {
        private const val MUK_REQUEST_CAPTURE_IMAGE = 1
        private const val MUK_REQUEST_GALLERY_IMAGE = 2
        private const val MUK_REQUEST_GENDER = 3
        private const val MUK_REQUEST_COUNTRY = 4
        const val MUK_GENDER_EXTRA = "MUK_GENDER_EXTRA"
        const val MUK_COUNTRY_EXTRA = "MUK_COUNTRY_EXTRA"
    }

}