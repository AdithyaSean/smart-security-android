package com.nextstep.smartsecurity.ui.gallery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nextstep.smartsecurity.R

class GalleryAdapter(private val context: Context, private val photos: List<Int>) :
    RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(photo: Int) {
            imageView.setImageResource(photo)
            itemView.setOnClickListener {
                val intent = Intent(context, PhotoViewActivity::class.java).apply {
                    putExtra("photo_res_id", photo)
                }
                context.startActivity(intent)
            }
        }
    }
}