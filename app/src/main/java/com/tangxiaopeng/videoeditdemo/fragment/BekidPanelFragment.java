package com.tangxiaopeng.videoeditdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qiniu.pili.droid.shortvideo.PLPaintView;
import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.AddPanelAdapter;
import com.tangxiaopeng.videoeditdemo.bean.EditPanelbean;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.view.PaintSelectorPanel;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 在视频上面添加文字
 */
public class BekidPanelFragment extends Fragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "current_page";
    private static final String TAG = "BekidPanelFragment";
    Unbinder unbinder;
    @BindView(R.id.paint_selector_panel)
    PaintSelectorPanel mPaintSelectorPanel;
    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    @BindView(R.id.ll_paint_selector_panel)
    LinearLayout mLlPaintSelectorPanel;


    public static List<EditPanelbean> mDataPanelList = new ArrayList<>();
    protected AddPanelAdapter mAddAdapter;
    @BindView(R.id.ivAddEdit)
    ImageView mIvAddEdit;
    @BindView(R.id.TvEditCancel)
    ImageView mTvEditCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panel_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initTextSelectorPanel();
        OnInitListener();
        initviewList();
    }

    private void initView() {
        if (mDataPanelList.size() == 0) {
            mLlPaintSelectorPanel.setVisibility(View.VISIBLE);
        }
//        mIvAddEdit.setOnClickListener(this);
//        TvEditCancel.setOnClickListener(this);
    }


    /**
     * 添加进去的到选中的列表中
     */
    private void initviewList() {
        //type音乐（1）和录音（2）
        mAddAdapter = new AddPanelAdapter(getActivity(), mRecyclerView, 2);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAddAdapter);
        mAddAdapter.notifyDataSetChanged(mDataPanelList);

        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAddAdapter.setOnUpdateDataListener(new AddPanelAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                additem(mDataPanelList.get(position).getPLPaintView());
            }
        });
    }


    private void initTextSelectorPanel() {
        mPaintSelectorPanel.setOnPaintSelectorListener(new PaintSelectorPanel.OnPaintSelectorListener() {
            @Override
            public void onViewClosed() {
                ((BekidMainActivity) getActivity()).setPanelVisibility(mPaintSelectorPanel, false, false);
                ((BekidMainActivity) getActivity()).setPaintEnable();
            }

            @Override
            public void onPaintColorSelected(int color) {
                ((BekidMainActivity) getActivity()).setPaintColor(color);
            }

            @Override
            public void onPaintSizeSelected(int size) {
                ((BekidMainActivity) getActivity()).setPaintSize(size);
            }

            @Override
            public void onPaintUndoSelected() {
                ((BekidMainActivity) getActivity()).undo();
            }

            @Override
            public void onPaintClearSelected() {
                ((BekidMainActivity) getActivity()).clear();
            }
        });
    }

    public void OnInitListener() {
        ((BekidMainActivity) getActivity()).getPanelVisibility();
        mPaintSelectorPanel.setup();
    }

    public void additem(PLPaintView mPaintView) {
        mLlPaintSelectorPanel.setVisibility(View.VISIBLE);
        ((BekidMainActivity) getActivity()).AddCurrentPanel(mPaintView);
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
            case R.id.ivAddEdit:
                mLlPaintSelectorPanel.setVisibility(View.VISIBLE);
                mIvAddEdit.setVisibility(View.GONE);
                break;
            case R.id.TvEditCancel:
                mLlPaintSelectorPanel.setVisibility(View.GONE);
                mIvAddEdit.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
