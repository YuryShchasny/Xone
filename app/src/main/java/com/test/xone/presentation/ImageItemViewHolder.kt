package com.test.xone.presentation

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.test.xone.R

class ImageItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val imageViewItem: ImageView = view.findViewById(R.id.imageViewItem)
    val frameLayoutDeleteCheck: FrameLayout = view.findViewById(R.id.frameLayoutDeleteCheck)
    val imageViewDeleteCrossCheck: ImageView = view.findViewById(R.id.imageViewDeleteCrossCheck)
}