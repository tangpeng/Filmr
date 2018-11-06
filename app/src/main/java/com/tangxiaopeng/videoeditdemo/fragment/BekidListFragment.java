package com.tangxiaopeng.videoeditdemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.MainAdapter;
import com.tangxiaopeng.videoeditdemo.bean.videobean;
import com.tangxiaopeng.videoeditdemo.utils.GetPathFromUri;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.Tools;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * bekid
 * Created by JY
 * Date：17/2/20
 * Time：下午5:01
 */
public class BekidListFragment extends BaseFragment implements View.OnClickListener, SwipeItemClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidNewFragment";
    //    @BindView(R.id.lltopFilmr)
//    LinearLayout lltopFilmr;
    Unbinder unbinder;

    private static final int SLICE_COUNT = 8;

    @BindView(R.id.addVideoFromLocal)
    ImageView mAddVideoFromLocal;
    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;

    private View addViewtopFilmr;

    private int mSlicesTotalLength;

    protected MainAdapter mAdapter;

    public static List<videobean> mDataVideoList = new ArrayList<>();

    /*Fragment的传参方式(通过Bundle对象来传递)
    *采用这种传参方式可以保证用户在横竖屏切换时所
    * 传递的参数不会丢失
    */
    public static BekidListFragment getInstance(String data) {
        BekidListFragment rightFragment = new BekidListFragment();
        Bundle bundle = new Bundle();
        //将需要传递的字符串以键值对的形式传入bundle
        bundle.putString("data", data);
        rightFragment.setArguments(bundle);
        return rightFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filmr_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i(TAG, "init");
        initviewList();
        findViews();
    }

    private void initviewList() {
        mAdapter = new MainAdapter(getActivity(), mRecyclerView);

        mRecyclerView.setLayoutManager(createLayoutManager(false));
        mRecyclerView.setSwipeItemClickListener(this);
        mRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽和侧滑删除，更新UI和数据源。
        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged(mDataVideoList);
        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAdapter.setOnUpdateDataListener(new MainAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                addItem(mDataVideoList.get(position).getVideoUrl());
            }
        });
//        mAdapter.setOnScrollListener(new BaseAdapter.OnScrollListener() {
//            @Override
//            public void update(boolean canScroll) {
//                MyLog.i(TAG,"canScroll="+canScroll);
//                getLayoutManager(canScroll);
//            }
//        });
    }

    private void findViews() {

        mAddVideoFromLocal.setOnClickListener(this);
        String data = getArguments().getString("data");
        addItem(data);
    }

    /**
     * 添加新的一项
     *
     * @param mPath
     */
    private void addItem(String mPath) {
        MyLog.i(TAG, "videoPath=" + mPath);
        mAdapter.DragComplete();

        PLMediaFile mMediaFile;
        mMediaFile = new PLMediaFile(mPath);
        videobean mvideobean = new videobean();
        mvideobean.setVideoSize(mMediaFile.getDurationMs());
        mvideobean.setStartTime(0);
        mvideobean.setEndTime(mMediaFile.getDurationMs());
        mvideobean.setVideoUrl(mPath);
        mvideobean.setGetAllTime(mMediaFile.getDurationMs());//这个时候，总时间和裁切的时间一致
        mDataVideoList.add(mvideobean);

        mAdapter.notifyDataSetChanged(mDataVideoList);

        ((BekidMainActivity) getActivity()).AddLocalDivisionVideo(mMediaFile);//进度条的分割
    }


    /**
     * 监听拖拽和侧滑删除，更新UI和数据源。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            MyLog.i(TAG, "onItemMove");
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
                return false;
            }
            ;
            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = srcHolder.getAdapterPosition() - mRecyclerView.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - mRecyclerView.getHeaderItemCount();

            Collections.swap(mDataVideoList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int adapterPosition = srcHolder.getAdapterPosition();
            int position = adapterPosition - mRecyclerView.getHeaderItemCount();

            if (mRecyclerView.getHeaderItemCount() > 0 && adapterPosition == 0) { // HeaderView。
//                    mRecyclerView.removeHeaderView(mHeaderView);
//                    Toast.makeText(DragSwipeListActivity.this, "HeaderView被删除。", Toast.LENGTH_SHORT).show();
            } else { // 普通Item。
                mDataVideoList.remove(position);
                mAdapter.notifyItemRemoved(position);
//                    Toast.makeText(DragSwipeListActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                MyLog.i(TAG, "状态：拖拽");
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                MyLog.i(TAG, "状态：滑动删除");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                MyLog.i(TAG, "状态：手指松开");
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(getActivity(), R.drawable.select_white));

                mAdapter.DragComplete();
                mAdapter.notifyDataSetChanged(mDataVideoList);
                ((BekidMainActivity) getActivity()).updateSeekBar(0);//回到第一段
                ((BekidMainActivity) getActivity()).reIdleReStartPlay();
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addVideoFromLocal:
                if(mDataVideoList.size()==0){
                    addLoacalVideo();
                }else{
                    Tools.showToast(getActivity(),"添加多段视频功能，等七牛支持就开放，功能已经做完");
                }
                break;
            default:
                break;
        }
    }


    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(getActivity(), R.color.index_color));
    }


    private String formatTime(long timeMs) {
        return String.format(Locale.CHINA, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMs),
                TimeUnit.MILLISECONDS.toSeconds(timeMs) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }

    @Override
    public void onStop() {
        super.onStop();

//        ((BekidMainActivity) getActivity()).reIdleStartPlay();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addLoacalVideo() {
        Intent intentvideo = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentvideo.setAction(Intent.ACTION_GET_CONTENT);
            intentvideo.setType("video/*");
        } else {
            intentvideo.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intentvideo.addCategory(Intent.CATEGORY_OPENABLE);
            intentvideo.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intentvideo, "选择要导入的视频"), 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String selectedFilepath = GetPathFromUri.getPath(getActivity(), data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (selectedFilepath != null && !"".equals(selectedFilepath)) {
                try {
                    addItem(selectedFilepath);
                } catch (Exception e) {
                    Log.i(TAG, "e=" + e.getMessage());
                }
            }
        } else {
        }
    }

    @Override
    public void onItemClick(View itemView, int position) {

    }


}
