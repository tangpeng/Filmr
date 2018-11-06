package com.tangxiaopeng.videoeditdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.AddTextAdapter;
import com.tangxiaopeng.videoeditdemo.bean.EditTextbean;
import com.tangxiaopeng.videoeditdemo.utils.CommonUtil;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.Tools;
import com.tangxiaopeng.videoeditdemo.view.ColorPickerView;
import com.tangxiaopeng.videoeditdemo.view.StrokedTextView;
import com.tangxiaopeng.videoeditdemo.view.TextSelectorPanel;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 在视频上面添加文字
 */
public class BekidTextFragment extends BaseFragment implements View.OnClickListener {

    public static String CURRENT_PAGE = "curreage";
    private static final String TAG = "BekidTextFragment";
    Unbinder unbinder;


    @BindView(R.id.TvSpeedTitle)
    TextView mTvSpeedTitle;

    @BindView(R.id.text_selector_panel)
    TextSelectorPanel mTextSelectorPanel;

    @BindView(R.id.ivEditTextCancel)
    ImageView mIvEditTextCancel;
    @BindView(R.id.ivEditTextSure)
    ImageView mIvEditTextSure;
    @BindView(R.id.etEditTextSure)
    EditText mEtEditTextSure;
    @BindView(R.id.cpvEditText)
    ColorPickerView mCpvEditText;

    //拖动进度条，选中的颜色
    int mSelectcolor = Color.WHITE;
    @BindView(R.id.llAddEditText)
    LinearLayout mLlAddEditText;
    @BindView(R.id.ivAddEditText)
    ImageView mIvAddEditText;

    private View addViewtopFilmr;

    private int mSlicesTotalLength;
    private static final int SLICE_COUNT = 8;

    private int idleTextShow = 3;//默认文字显示3秒

    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    public static List<EditTextbean> mDataTextList = new ArrayList<>();
    protected AddTextAdapter mAddAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTextSelectorPanel();
        OnInitListener();
        initData();
        initviewList();
    }

    private void initData() {
        if (mDataTextList.size() == 0) {
            mLlAddEditText.setVisibility(View.VISIBLE);
        }
    }

    public void initAddText(StrokedTextView strokedTextView) {
        mAddAdapter.notifyDataSetChanged(mDataTextList);
    }

    public void additem(String addtext) {
        ((BekidMainActivity) getActivity()).AddText(mSelectcolor, addtext);
    }

    private void OnInitListener() {
        mIvEditTextCancel.setOnClickListener(this);
        mIvEditTextSure.setOnClickListener(this);
        mIvAddEditText.setOnClickListener(this);
    }

    /**
     * 添加进去的到选中的列表中
     */
    private void initviewList() {
        //type音乐（1）和录音（2）
        mAddAdapter = new AddTextAdapter(getActivity(), mRecyclerView, 2);

        mRecyclerView.setLayoutManager(createLayoutManager(false));
        mRecyclerView.setAdapter(mAddAdapter);
        mAddAdapter.notifyDataSetChanged(mDataTextList);

        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAddAdapter.setOnUpdateDataListener(new AddTextAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                additem(mDataTextList.get(position).getStrokedTextView().getText().toString());
            }
        });
    }

    private void initTextSelectorPanel() {
        mCpvEditText.setOnColorPickerChangeListener(new ColorPickerView.OnColorPickerChangeListener() {
            @Override
            public void onColorChanged(ColorPickerView picker, int color) {
                MyLog.i(TAG, "color=" + color);
                mSelectcolor = color;
                mEtEditTextSure.setTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(ColorPickerView picker) {

            }

            @Override
            public void onStopTrackingTouch(ColorPickerView picker) {

            }
        });
        mTextSelectorPanel.setOnTextSelectorListener(new TextSelectorPanel.OnTextSelectorListener() {
            @Override
            public void onTextSelected(StrokedTextView textView) {
                MyLog.i(TAG, "onTextSelected");
//                ((BekidMainActivity) getActivity()).AddText(textView);
            }

            @Override
            public void onViewClosed() {
                ((BekidMainActivity) getActivity()).setPanelVisibility(mTextSelectorPanel, false, false);
            }
        });
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
            case R.id.ivEditTextSure:
                if (mEtEditTextSure.getText().toString().equals("")) {
                    Tools.showToast(getActivity(), "文字不能为空");
                    return;
                }
                CommonUtil.HideKeyboard(mEtEditTextSure);
                mLlAddEditText.setVisibility(View.GONE);
                additem(mEtEditTextSure.getText().toString());
                mEtEditTextSure.setText("");
                ((BekidMainActivity) getActivity()).reIdleReStartPlay();
                break;
            case R.id.ivEditTextCancel:
                mEtEditTextSure.setTextColor(getResources().getColor(R.color.common_black));
                mEtEditTextSure.setText("");
                mEtEditTextSure.setFocusable(true);
                mLlAddEditText.setVisibility(View.GONE);
                break;
            case R.id.ivAddEditText:
                mLlAddEditText.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
