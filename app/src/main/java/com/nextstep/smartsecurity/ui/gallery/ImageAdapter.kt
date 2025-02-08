package com.nextstep.smartsecurity.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.data.Image

class ImageAdapter(
    private val onMarkKnown: (Image, Boolean) -> Unit
) : ListAdapter<Image, ImageAdapter.ImageViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view, onMarkKnown)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    class ImageViewHolder(
        itemView: View,
        private val onMarkKnown: (Image, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val unknownOverlay: View = itemView.findViewById(R.id.unknownOverlay)
        private val unknownText: TextView = itemView.findViewById(R.id.unknownText)
        private val markButton: ImageButton = itemView.findViewById(R.id.markButton)
        private var currentImage: Image? = null

        init {
            itemView.setOnLongClickListener {
                currentImage?.let { image ->
                    markButton.visibility = if (markButton.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                true
            }

            markButton.setOnClickListener {
                currentImage?.let { image ->
                    onMarkKnown(image, !image.isKnown)
                    markButton.visibility = View.GONE
                }
            }
        }

        fun bind(image: Image) {
            currentImage = image
            
            // Load the image using Glide
            Glide.with(itemView.context)
                .load(image.imageUrl)
                .into(imageView)

            // Update UI based on known/unknown status
            if (!image.isKnown) {
                unknownOverlay.visibility = View.VISIBLE
                unknownText.visibility = View.VISIBLE
            } else {
                unknownOverlay.visibility = View.GONE
                unknownText.visibility = View.GONE
            }
            
            // Initially hide the mark button
            markButton.visibility = View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}
