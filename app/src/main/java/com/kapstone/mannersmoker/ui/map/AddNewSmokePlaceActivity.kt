package com.kapstone.mannersmoker.ui.map

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.kapstone.mannersmoker.R
import com.kapstone.mannersmoker.base.BaseActivity2
import com.kapstone.mannersmoker.databinding.ActivityAddNewSmokePlaceBinding

class AddNewSmokePlaceActivity : BaseActivity2<ActivityAddNewSmokePlaceBinding>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_add_new_smoke_place

    // onCreate에서 xml binding 이후 호출됨
    override fun initStartView() {
        binding.addNewSmokePlaceCancel.setOnClickListener {
            onBackPressed()
        }
        binding.photoImageView.setOnClickListener {
            takePicture()
        }
    }


    private fun takePicture() {
        val imageTakeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (imageTakeIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            val extras : Bundle? = data?.extras
            val imageBitmap : Bitmap? = extras?.get("data") as Bitmap?
            binding.photoImageView.setImageBitmap(imageBitmap)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private const val REQUEST_IMAGE_CODE = 101
    }

}


