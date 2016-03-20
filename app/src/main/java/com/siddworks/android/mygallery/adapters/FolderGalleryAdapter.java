package com.siddworks.android.mygallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.siddworks.android.mygallery.R;
import com.siddworks.android.mygallery.util.ImageGalleryUtils;

import java.util.List;

/**
 * Created by SIDD on 19-Mar-16.
 */
public class FolderGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Member Variables
    private final List<String> mImages;
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
    public FolderGalleryAdapter(List<String> images) {
        mImages = images;
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

        String image = mImages.get(position);
        holder.mTextView.setText(image);
//        setUpImage(holder.mTextView, image);

        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                if(adapterPos != RecyclerView.NO_POSITION){
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(adapterPos);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mImages != null) {
            return mImages.size();
        } else {
            return 0;
        }
    }

    // region Helper Methods
    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
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