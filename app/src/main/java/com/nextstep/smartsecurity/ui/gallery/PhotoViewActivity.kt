package com.nextstep.smartsecurity.ui.gallery

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.nextstep.smartsecurity.R

class PhotoViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        val imageView: ImageView = findViewById(R.id.image_view)
        val photoResId = intent.getIntExtra("photo_res_id", -1)
        if (photoResId != -1) {
            imageView.setImageResource(photoResId)
        }
    }
}