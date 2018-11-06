package com.tangxiaopeng.videoeditdemo.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.qiniu.pili.droid.shortvideo.PLTextView;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.CardPagerAdapter;
import com.tangxiaopeng.videoeditdemo.bean.CardItem;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.view.ShadowTransformer;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.filterNum;

/**
 * 滤镜
 */
public class BekidFilterFragment extends Fragment {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidFilterFragment";
    @BindView(R.id.recycler_view_filter)
    RecyclerView mRecyclerViewFilter;
    @BindView(R.id.vgFilterFragment)
    ViewPager mViewPager;
    Unbinder unbinder;


    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;


    private PLImageView mCurImageView;
    private PLTextView mCurTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findPagerViews();
        OnInitListener();
//        ((BekidMainActivity) getActivity()).reIdleResumePlay();
    }

    private void OnInitListener() {

    }


    /**
     * @dec 选择不同的滤镜
     * @author fanqie
     * @date 2018/8/22 18:47
     */
    private void findPagerViews() {
        mCardAdapter = new CardPagerAdapter();
        MyLog.i(TAG, "filter.length=" + filterNum.length);
        for (int i = 0; i < filterNum.length + 1; i++) {
            try {
                if (i == 0) {
                    Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("filters/none.png"));
                    mCardAdapter.addCardItem(new CardItem(bitmap, "None"));
                } else {
                    final PLBuiltinFilter filter = filterNum[i - 1];
                    InputStream is = getActivity().getAssets().open(filter.getAssetFilePath());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mCardAdapter.addCardItem(new CardItem(bitmap, filter.getName()));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

        mViewPager.setAdapter(mCardAdapter);

        MyLog.i(TAG,"缩放和透明度渐变的动画PageTransformer");
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int onPageSelected) {
                MyLog.i(TAG, "onPageSelected=" + onPageSelected);
                if (onPageSelected == 0) {
                    ((BekidMainActivity) getActivity()).SelectedFilter(null);
                } else {
                    final PLBuiltinFilter filter = filterNum[onPageSelected - 1];
                    ((BekidMainActivity) getActivity()).SelectedFilter(filter.getName());
                }
                ((BekidMainActivity) getActivity()).isPlayfilter();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void initFiltersList() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerViewFilter.setLayoutManager(layoutManager);
//        mRecyclerViewFilter.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
    }

    private void hideViewBorder() {
        if (mCurTextView != null) {
            mCurTextView.setBackgroundResource(0);
            mCurTextView = null;
        }

        if (mCurImageView != null) {
            mCurImageView.setBackgroundResource(0);
            mCurImageView = null;
        }
    }

    private void showImageViewBorder(PLImageView imageView) {
        hideViewBorder();
        mCurImageView = imageView;
        mCurImageView.setBackgroundResource(R.drawable.border_text_view);
    }


    private class FilterListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private PLBuiltinFilter[] mFilters;

        public FilterListAdapter(PLBuiltinFilter[] filters) {
            this.mFilters = filters;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("filters/none.png"));
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BekidMainActivity) getActivity()).SelectedFilter(null);
                        }
                    });
                    return;
                }

                final PLBuiltinFilter filter = mFilters[position - 1];
                holder.mName.setText(filter.getName());
                InputStream is = getActivity().getAssets().open(filter.getAssetFilePath());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BekidMainActivity) getActivity()).SelectedFilter(filter.getName());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mFilters != null ? mFilters.length + 1 : 0;
        }
    }

    private class FilterItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public FilterItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        MyLog.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i(TAG, "onResume");
    }
}
