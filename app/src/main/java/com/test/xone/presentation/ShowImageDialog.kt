package com.test.xone.presentation

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import com.test.xone.R

class ShowImageDialog(context: Context, private val imageUri: String): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_show_image)
        window?.setBackgroundDrawableResource(R.color.transparent)
        findViewById<ImageView>(R.id.imageViewShow).setImageURI(Uri.parse(imageUri))
    }
    override fun onBackPressed() {
        this.dismiss()
    }
}