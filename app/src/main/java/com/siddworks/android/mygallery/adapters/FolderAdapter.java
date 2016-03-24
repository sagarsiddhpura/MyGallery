package com.siddworks.android.mygallery.adapters;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.siddworks.android.mygallery.R;
import com.siddworks.android.mygallery.ui.BrowseActivity;
import com.siddworks.android.mygallery.util.ImageGalleryUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by SIDD on 19-Mar-16.
 */
public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Member Variables
    private final File[] mFiles;
    private int mGridItemWidth;
    private int mGridItemHeight;
    private OnImageClickListener mOnImageClickListener;
    // endregion

    // region Interfaces
    public interface OnImageClickListener {
        void onImageClick(int position);
    }
    // endregion

    // region Constructors
    public FolderAdapter(File[] images) {
        mFiles = images;
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folder_thumbnail, viewGroup, false);
        v.setLayoutParams(getGridItemLayoutParams(v));

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ImageViewHolder holder = (ImageViewHolder) viewHolder;

        File file = mFiles[position];
        String name = file.getName();
        holder.mTextView.setText(name);

        if (file.isFile()) {
            String currentFileNameExtension = name.substring(
                    name.lastIndexOf('.')+1, name.length());
            if(BrowseActivity.imageExtensions.contains(currentFileNameExtension.toLowerCase())) {
                setUpImage(holder.mImageView, file);
            } else if(BrowseActivity.videoExtensions.contains(currentFileNameExtension.toLowerCase())) {
                setUpVideo(holder.mImageView, file);
            }

        } else if (file.isDirectory()) {
            setUpImage(holder.mImageView, R.mipmap.ic_folder);
        }

        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(adapterPos);
                    }
                }
            }
        });
    }

    private void setUpVideo(ImageView mImageView, File file) {
        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
        mImageView.setImageBitmap(bMap);
    }

    @Override
    public int getItemCount() {
        if (mFiles != null) {
            return mFiles.length;
        } else {
            return 0;
        }
    }

    // region Helper Methods
    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
    }

    private void setUpImage(ImageView iv, File imageUrl) {
        Picasso.with(iv.getContext())
                .load(imageUrl)
                .resize(mGridItemWidth, mGridItemHeight)
                .centerCrop()
                .into(iv);
    }

    private void setUpImage(ImageView iv, int imageUrl) {
        Picasso.with(iv.getContext())
                .load(imageUrl)
                .resize(mGridItemWidth, mGridItemHeight)
                .centerCrop()
                .into(iv);
    }

    private ViewGroup.LayoutParams getGridItemLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int screenWidth = ImageGalleryUtils.getScreenWidth(view.getContext());
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(view.getContext())) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mGridItemWidth = screenWidth / numOfColumns;
        mGridItemHeight = screenWidth / numOfColumns;

        layoutParams.width = mGridItemWidth;
        layoutParams.height = mGridItemHeight;

        return layoutParams;
    }

//    private void setUpImage(ImageView iv, String imageUrl) {
//        if (!TextUtils.isEmpty(imageUrl)) {
//            Picasso.with(iv.getContext())
//                    .load(imageUrl)
//                    .resize(mGridItemWidth, mGridItemHeight)
//                    .centerCrop()
//                    .into(iv);
//        } else {
//            iv.setImageDrawable(null);
//        }
//    }
    // endregion

    // region Inner Classes

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        // region Member Variables
        private final ImageView mImageView;
        private final TextView mTextView;
        private final FrameLayout mFrameLayout;
        // endregion

        // region Constructors
        public ImageViewHolder(final View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.iv);
            mTextView = (TextView) view.findViewById(R.id.tv);
            mFrameLayout = (FrameLayout) view.findViewById(R.id.fl);
        }
        // endregion
    }

    // endregion
}