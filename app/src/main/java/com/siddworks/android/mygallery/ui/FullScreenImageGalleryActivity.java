package com.siddworks.android.mygallery.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.siddworks.android.mygallery.R;
import com.siddworks.android.mygallery.adapters.FullScreenImageGalleryAdapter;
import com.siddworks.android.mygallery.util.PaletteColorType;

import java.util.ArrayList;
import java.util.List;

public class FullScreenImageGalleryActivity extends AppCompatActivity {

    // region Member Variables
    private List<String> mImages;
    private int mPosition;
    private PaletteColorType mPaletteColorType;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    // endregion

    // region Listeners
    private final ViewPager.OnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(position);

                setActionBarTitle(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private String mPath;
    // endregion

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_screen_image_gallery);

        bindViews();

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList("images");
                mPaletteColorType = (PaletteColorType) extras.get("palette_color_type");
                mPosition = extras.getInt("position");
                mPath = extras.getString("path", null);
            }
        }

        setUpViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListeners();
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

    // region Helper Methods
    private void bindViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpViewPager() {
        ArrayList<String> images = new ArrayList<>();
        images.addAll(mImages);

        FullScreenImageGalleryAdapter fullScreenImageGalleryAdapter =
                new FullScreenImageGalleryAdapter(images, mPaletteColorType, mPath);
        mViewPager.setAdapter(fullScreenImageGalleryAdapter);
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
        mViewPager.setCurrentItem(mPosition);

        setActionBarTitle(mPosition);
    }

    private void setActionBarTitle(int position) {
        if (mViewPager != null && mImages.size() > 1) {
            int totalPages = mViewPager.getAdapter().getCount();

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                String imageName = mImages.get(position);
                if(imageName.contains(".")) {
                    int i = imageName.lastIndexOf(".");
                    imageName = imageName.substring(0, i);
                }
                actionBar.setTitle(String.format("%d/%d", (position + 1), totalPages)+ "  " +
                        imageName);
                final Drawable upArrow = getResources().getDrawable(R.drawable.ic_up);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    private void removeListeners() {
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
    }
    // endregion
}