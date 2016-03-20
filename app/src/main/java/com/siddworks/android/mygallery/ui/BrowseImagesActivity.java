package com.siddworks.android.mygallery.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.siddworks.android.mygallery.R;
import com.siddworks.android.mygallery.adapters.ImageGalleryAdapter;
import com.siddworks.android.mygallery.util.GridSpacesItemDecoration;
import com.siddworks.android.mygallery.util.ImageGalleryUtils;
import com.siddworks.android.mygallery.util.PaletteColorType;

import java.util.ArrayList;

public class BrowseImagesActivity extends AppCompatActivity implements ImageGalleryAdapter.OnImageClickListener {

    // region Member Variables
    private ArrayList<String> mImages;
    private PaletteColorType mPaletteColorType;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private String mPath;
    private String TAG = "BrowseImagesActivity";
    private String mTitle;
    // endregion

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse_images);

        bindViews();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList("images");
                mPaletteColorType = (PaletteColorType) extras.get("palette_color_type");
                mPath = extras.getString("path", null);
                mTitle = extras.getString("title", "");
            }
        }

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_up);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            actionBar.setTitle(mTitle);
        }

        setUpRecyclerView();
    }
    // endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {

        Intent intent = new Intent(BrowseImagesActivity.this, FullScreenImageGalleryActivity.class);

        intent.putStringArrayListExtra("images", mImages);
        intent.putExtra("position", position);
        intent.putExtra("path", mPath);
        if (mPaletteColorType != null) {
            intent.putExtra("palette_color_type", mPaletteColorType);
        }

        startActivity(intent);
    }
    // endregion

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpRecyclerView() {
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(this)) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(BrowseImagesActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(mImages, mPath);
        imageGalleryAdapter.setOnImageClickListener(this);

        mRecyclerView.setAdapter(imageGalleryAdapter);
    }
    // endregion
}
