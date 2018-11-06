package com.tangxiaopeng.videoeditdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.AddChartAdapter;
import com.tangxiaopeng.videoeditdemo.bean.EditChartbean;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.view.ImageChartSelectorPanel;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tangxiaopeng.videoeditdemo.BekidMainActivity.mDurationMsAll;

/**
 * 贴图
 */
public class BekidChartFragment extends BaseFragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidChartFragment";
    Unbinder unbinder;
    @BindView(R.id.image_selector_panel)
    ImageChartSelectorPanel mImageSelectorPanel;

    @BindView(R.id.ivAddEditChart)
    ImageView mIvAddEditChart;
    @BindView(R.id.ivEditChartCancel)
    ImageView mIvEditChartCancel;
    @BindView(R.id.TvChartTitle)
    TextView mTvChartTitle;
    @BindView(R.id.ivEditChartSure)
    ImageView mIvEditChartSure;
    @BindView(R.id.llAddEditChart)
    LinearLayout mLlAddEditChart;



    private long idleTextShow = mDurationMsAll;//贴图文字，默认显示全部


    private int mSlicesTotalLength;
    private static final int SLICE_COUNT = 8;

//    private PLImageView getSelectImageView;//选择的贴图

    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    public static List<EditChartbean> mDataChartList = new ArrayList<>();
    protected AddChartAdapter mAddAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        OnInitListener();
        initviewList();
    }

    private void OnInitListener() {
        mIvAddEditChart.setOnClickListener(this);
        mIvEditChartCancel.setOnClickListener(this);
        mIvEditChartSure.setOnClickListener(this);
    }

    private void initView() {

        if (mDataChartList.size() == 0) {
            mLlAddEditChart.setVisibility(View.VISIBLE);
        }
        mImageSelectorPanel.setOnImageSelectedListener(new ImageChartSelectorPanel.OnImageSelectedListener() {
            @Override
            public void onImageSelected(int imageId) {
                MyLog.i(TAG, "imageId=" + imageId);
                additem(imageId);
            }

        });
    }

    /**
     * 添加
     */
    private void additem(int imageId) {
        MyLog.i(TAG, "imageId=" + imageId);
//        final PLImageView imageView = new PLImageView(getActivity());
//        imageView.setImageResource(imageId);
//        imageView.setTag(imageId);
//      getSelectImageView=imageView;

        mLlAddEditChart.setVisibility(View.GONE);
        ((BekidMainActivity) getActivity()).AddCurrentChart(imageId);
    }

    /**
     * 添加进去的到选中的列表中
     */
    private void initviewList() {
        //type音乐（1）和录音（2）
        mAddAdapter = new AddChartAdapter(getActivity(), mRecyclerView);

        mRecyclerView.setLayoutManager(createLayoutManager(false));
        mRecyclerView.setAdapter(mAddAdapter);
        mAddAdapter.notifyDataSetChanged(mDataChartList);

        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAddAdapter.setOnUpdateDataListener(new AddChartAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                additem(mDataChartList.get(position).getIvImageId());
                ((BekidMainActivity) getActivity()).reIdleReStartPlay();
            }
        });
    }


    /**
     * @dec 添加贴图到列表中
     * @author fanqie
     * @date 2018/9/6 18:33
     */
    public void initAddChart(PLImageView imageView, int imageId) {

        mAddAdapter.notifyDataSetChanged(mDataChartList);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEditChartSure:
                mLlAddEditChart.setVisibility(View.GONE);
                break;
            case R.id.ivEditChartCancel:
                mLlAddEditChart.setVisibility(View.GONE);
                break;
            case R.id.ivAddEditChart:
                mLlAddEditChart.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
