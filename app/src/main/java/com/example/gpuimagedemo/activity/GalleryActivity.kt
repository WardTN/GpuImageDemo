package com.example.gpuimagedemo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.gpuimagedemo.GPUImageFilterTools
import com.example.gpuimagedemo.GPUImageView
import com.example.gpuimagedemo.R
import com.example.gpuimagedemo.filter.GPUImageFilter

class GalleryActivity : AppCompatActivity() {

    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.gpuimage) }
    private val seekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seekBar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
                gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<View>(R.id.button_choose_filter).setOnClickListener {
            GPUImageFilterTools.showDialog(this) { filter ->
                switchFilterTo(filter)
                gpuImageView.requestRender()
            }
        }
        findViewById<View>(R.id.button_save).setOnClickListener { saveImage() }

        startPhotoPicker()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> if (resultCode == RESULT_OK) {
                gpuImageView.setImage(data!!.data)
            } else {
                finish()
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE)
    }


    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        gpuImageView.saveToPictures("GPUImage", fileName) { uri ->
            Toast.makeText(this, "Saved: " + uri.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        if (gpuImageView.filter == null || gpuImageView.filter.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
            filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
            if (filterAdjuster!!.canAdjust()) {
                seekBar.visibility = View.VISIBLE
                filterAdjuster!!.adjust(seekBar.progress)
            } else {
                seekBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }
}
