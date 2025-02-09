package com.nextstep.smartsecurity.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nextstep.smartsecurity.R
import com.nextstep.smartsecurity.databinding.ItemCameraBinding
import com.nextstep.smartsecurity.service.CameraDiscoveryService.CameraInfo

class CameraAdapter(
    private val onCameraClick: (CameraInfo) -> Unit
) : ListAdapter<CameraInfo, CameraAdapter.CameraViewHolder>(CameraDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        return CameraViewHolder(
            ItemCameraBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CameraViewHolder(
        private val binding: ItemCameraBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCameraClick(getItem(position))
                }
            }
        }

        fun bind(camera: CameraInfo) {
            binding.apply {
                nameText.text = camera.name
                addressText.text = "${camera.host}:${camera.port}"
                
                val statusColor = if (camera.isReachable) {
                    R.color.online
                } else {
                    R.color.offline
                }
                statusIndicator.setColorFilter(
                    ContextCompat.getColor(root.context, statusColor)
                )
                
                val statusText = if (camera.isReachable) {
                    root.context.getString(R.string.online)
                } else {
                    root.context.getString(R.string.offline)
                }
                statusText.text = statusText

                root.alpha = if (camera.isReachable) 1.0f else 0.5f
            }
        }
    }

    object CameraDiffCallback : DiffUtil.ItemCallback<CameraInfo>() {
        override fun areItemsTheSame(oldItem: CameraInfo, newItem: CameraInfo): Boolean {
            return oldItem.cameraId == newItem.cameraId
        }

        override fun areContentsTheSame(oldItem: CameraInfo, newItem: CameraInfo): Boolean {
            return oldItem == newItem
        }
    }
}
