package com.tangxiaopeng.videoeditdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.R;


/**
 * @author fanqie
 * @dec 贴图和滤镜一致，这种可以借鉴，不错哦
 * @date 2018/8/27 18:21
 */
public class ImageChartSelectorPanel extends LinearLayout {
    private Context mContext;
    private RecyclerView mImageListView;
    private OnImageSelectedListener mOnImageSelectedListener;

    private static int[] imageId = {
            R.drawable.tu1, R.drawable.tu2, R.drawable.tu3, R.drawable.tu4, R.drawable.tu5, R.drawable.tu6
    };

    public ImageChartSelectorPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.panel_image_selector, this);
        mImageListView = (RecyclerView) view.findViewById(R.id.recycler_paint_image);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager mGridLayoutManager=new GridLayoutManager(context,3);
        mImageListView.setLayoutManager(mGridLayoutManager);
        mImageListView.setAdapter(new ImageListAdapter(imageId));
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        mOnImageSelectedListener = listener;
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int imageId);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private class ImageListAdapter extends RecyclerView.Adapter<ImageChartSelectorPanel.ItemViewHolder> {
        private int[] imageNum;

        public ImageListAdapter(int[] imagePaths) {
            this.imageNum = imagePaths;
        }

        @Override
        public ImageChartSelectorPanel.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ImageChartSelectorPanel.ItemViewHolder holder, final int position) {
//                final String imagePath = "filters/" + mImagePaths[position] + "/thumb.png";
//                InputStream is = mContext.getAssets().open(imagePath);
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mName.setVisibility(GONE);
                holder.mIcon.setImageResource(imageNum[position]);
                holder.mIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnImageSelectedListener != null) {
                            mOnImageSelectedListener.onImageSelected(imageNum[position]);
                        }
                    }
                });
        }

        @Override
        public int getItemCount() {
            return imageId.length;
        }
    }
}
