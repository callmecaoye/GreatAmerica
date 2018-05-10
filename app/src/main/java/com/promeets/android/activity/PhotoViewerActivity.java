package com.promeets.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.promeets.android.R;

public class PhotoViewerActivity extends BaseActivity {

    @Override
    public void initElement() {

    }

    @Override
    public void registerListeners() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        String url = getIntent().getStringExtra("url");
        String type = getIntent().getStringExtra("type");

        PhotoView photoView = (PhotoView) findViewById(R.id.main_image_view);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        progressBar.setVisibility(View.VISIBLE);
        if (type != null && type.toLowerCase().contains("image")) {
            Glide.with(this)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(photoView);
        } else
            finish();
    }
}
