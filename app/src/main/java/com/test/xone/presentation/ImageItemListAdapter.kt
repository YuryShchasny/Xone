package com.test.xone.presentation

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.test.xone.R
import com.test.xone.domain.ImageEntity

class ImageItemListAdapter(private val context: Context, private val deleteMode: Boolean = false) :
    ListAdapter<ImageEntity, ImageItemViewHolder>(ImageItemListDiffCallback()) {

    var onClickListener: ((ImageEntity, Int) -> Unit)? = null
    var onLongClickListener: ((ImageEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val inflater = LayoutInflater.from(context)
        return ImageItemViewHolder(inflater.inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val currentImage = getItem(position)
        holder.imageViewItem.setImageURI(Uri.parse(currentImage.imageUri))
        if (deleteMode) {
            holder.frameLayoutDeleteCheck.visibility = View.VISIBLE
            if (currentImage.isChecked) {
                holder.imageViewDeleteCrossCheck.visibility = View.VISIBLE
            } else {
                holder.imageViewDeleteCrossCheck.visibility = View.GONE
            }
        }
        else {
            holder.frameLayoutDeleteCheck.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            onClickListener?.let {
                it(currentImage, position)
            }
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.let {
                it(currentImage)
            }
            true
        }
    }
}