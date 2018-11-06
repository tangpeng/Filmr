package com.tangxiaopeng.videoeditdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangxiaopeng.videoeditdemo.BekidMainActivity;
import com.tangxiaopeng.videoeditdemo.R;
import com.tangxiaopeng.videoeditdemo.adapter.AddMusicAdapter;
import com.tangxiaopeng.videoeditdemo.bean.Musicbean;
import com.tangxiaopeng.videoeditdemo.dialog.ShowMusiciDialog;
import com.tangxiaopeng.videoeditdemo.utils.MyLog;
import com.tangxiaopeng.videoeditdemo.utils.Song;
import com.tangxiaopeng.videoeditdemo.utils.Tools;
import com.tangxiaopeng.videoeditdemo.utils.UPlayer;
import com.tangxiaopeng.videoeditdemo.view.RoundImageView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 添加音乐
 */
public class BekidMusicFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "BekidMusicFragment";

    @BindView(R.id.addMusic)
    ImageView mAddMusic;
    @BindView(R.id.btnMusicKit)
    Button mBtnMusicKit;
    @BindView(R.id.btnLocalImport)
    Button mBtnLocalImport;
    @BindView(R.id.ivCancel)
    ImageView mIvCancel;
    @BindView(R.id.rvFgMusic)
    RecyclerView mRvFgMusic;
    Unbinder unbinder;
    @BindView(R.id.tvSelectMusicHint)
    TextView mTvSelectMusicHint;
    @BindView(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    @BindView(R.id.addSelectMusic)
    LinearLayout mAddSelectMusic;

    private UPlayer mUPlayer;
    private List<Song> list;
    private TransListAdapter mAdapter;

    public static List<Musicbean> mDataMusicList = new ArrayList<>();
    protected AddMusicAdapter mAddAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_common, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        findAddMusicViews();
        initviewList();
        OnInitListener();
    }

    private void initData() {
        mUPlayer = new UPlayer();

        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Song mSong = new Song();
            //http://www.haolingsheng.com/lingsheng/i70mtz.htm  声音来源
            mSong.setPath("http://old.haolingsheng.com/2014/ring/000/096/6948a9a0080624158f4ad14fb0317051.mp3");//13s
            mSong.setDuration(256);
            mSong.setSinger("李宇春");
            mSong.setSong("下个路口见吗？");
            list.add(i, mSong);
        }
    }

    /**
     * @dec 本地音乐库
     * @author fanqie
     * @date 2018/9/12 15:53
     */
    private void findAddMusicViews() {
        mRvFgMusic.setLayoutManager(createLayoutManager(false));
        //添加Android自带的分割线
        mRvFgMusic.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAdapter = new TransListAdapter(getActivity(), list);
        mRvFgMusic.setAdapter(mAdapter);
    }

    /**
     * 添加进去的到选中的列表中
     */
    private void initviewList() {
        //type音乐（1）和录音（2）
        mAddAdapter = new AddMusicAdapter(getActivity(), mRecyclerView, 1);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽和侧滑删除，更新UI和数据源。
//        mRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item的手指状态，拖拽、侧滑、松开。

        mRecyclerView.setAdapter(mAddAdapter);
        mAddAdapter.notifyDataSetChanged(mDataMusicList);

        mRecyclerView.setLongPressDragEnabled(false); // 长按拖拽，默认关闭。
        mRecyclerView.setItemViewSwipeEnabled(false); // 滑动删除，默认关闭。

        mAddAdapter.setOnUpdateDataListener(new AddMusicAdapter.OnUpdateDataListener() {
            @Override
            public void updateData(int position) {
                mTvSelectMusicHint.setVisibility(View.GONE);
                addItem(mDataMusicList.get(position).getMusicUrl());
            }
        });
    }

    private void OnInitListener() {

        mAddMusic.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mBtnMusicKit.setOnClickListener(this);
        mBtnLocalImport.setOnClickListener(this);
    }

    private class TransViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mRivMusicThumb;
        ImageView mRivMusicThumbPlay;
        TextView mTvMusicThumbName;
        TextView mTvMusicThumbAuthor;
        ImageView mIvMusicThumbAdd;

        public TransViewHolder(View itemView) {
            super(itemView);
            mRivMusicThumb = (RoundImageView) itemView.findViewById(R.id.rivMusicThumb);
            mRivMusicThumbPlay = (ImageView) itemView.findViewById(R.id.rivMusicThumbPlay);
            mTvMusicThumbName = (TextView) itemView.findViewById(R.id.tvMusicThumbName);
            mTvMusicThumbAuthor = (TextView) itemView.findViewById(R.id.tvMusicThumbAuthor);
            mIvMusicThumbAdd = (ImageView) itemView.findViewById(R.id.ivMusicThumbAdd);
        }
    }

    private int selectPlayPostion = -1;

    private class TransListAdapter extends RecyclerView.Adapter<TransViewHolder> {
        private List<Song> lists;

        public TransListAdapter(Context context, List<Song> lists) {
            this.lists = lists;
        }

        @Override
        public TransViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.item_music_list, parent, false);
            TransViewHolder viewHolder = new TransViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final TransViewHolder holder, final int position) {

            holder.mTvMusicThumbName.setText(lists.get(position).getSong());
            holder.mTvMusicThumbAuthor.setText(lists.get(position).getSinger());
            Tools.setImageByUrlGlide(getActivity(), "", holder.mRivMusicThumb, R.drawable.ic_launcher);
            holder.mRivMusicThumbPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mRivMusicThumbPlay.setImageResource(R.drawable.u1);

                    MyLog.i(TAG, selectPlayPostion + "=播放音乐" + position);
                    ((BekidMainActivity) getActivity()).pausePlayback();
                    ((BekidMainActivity) getActivity()).pausePlaybackView();

                    if (selectPlayPostion == position) {
                        selectPlayPostion = -1;
                        holder.mRivMusicThumbPlay.setImageResource(R.drawable.u2);
                    } else {
                        selectPlayPostion = position;
                        holder.mRivMusicThumbPlay.setImageResource(R.drawable.u1);
                        toUploadDialog(lists.get(position).getPath(), holder.mRivMusicThumbPlay);
                    }
                    notifyDataSetChanged();
                }
            });
            holder.mIvMusicThumbAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddSelectMusic.setVisibility(View.GONE);
                    mTvSelectMusicHint.setVisibility(View.GONE);
                    addItem(lists.get(position).getPath());

                }
            });
            dealtype(position, holder);

        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

    }

    private void dealtype(final int position, TransViewHolder holder) {
        if (selectPlayPostion == position) {
            holder.mRivMusicThumbPlay.setImageResource(R.drawable.u1);
        } else {
            holder.mRivMusicThumbPlay.setImageResource(R.drawable.u2);
        }
    }

    /**
     * 添加新的一项
     *
     * @param mPath
     */
    public void addItem(String mPath) {
        if (mDataMusicList.size() > 0) {
            ((BekidMainActivity) getActivity()).reIdleReStartPlay();
        }
        String DurationMs = mUPlayer.duration(mPath);//毫秒计算
        Musicbean mMusicbean = new Musicbean();//添加参数
        mMusicbean.setMusicSize(Long.parseLong(DurationMs));
        mMusicbean.setStartTime(0);
        mMusicbean.setEndTime(Long.parseLong(DurationMs));
        mMusicbean.setMusicUrl(mPath);
        mMusicbean.setGetAllTime(Long.parseLong(DurationMs));

        if (mDataMusicList.size() > 0) {
            //起始位置，为上一段的起始时间+上一段的播放时间
            mMusicbean.setStartInsertTime(mDataMusicList.get(mDataMusicList.size() - 1).getStartInsertTime() + mDataMusicList.get(mDataMusicList.size() - 1).getMusicSize());
        }
        mDataMusicList.add(mMusicbean);
        mAddAdapter.notifyDataSetChanged(mDataMusicList);
        ((BekidMainActivity) getActivity()).addIndexMusic(mDataMusicList.size() - 1, Integer.parseInt(DurationMs));

    }

    public ShowMusiciDialog dialogUpload = null;// 弹出d对话框

    public void toUploadDialog(String url, final ImageView mRivMusicThumbPlay) {
        ShowMusiciDialog.OnSureClickListener mOnSureClickListener = new ShowMusiciDialog.OnSureClickListener() {
            @Override
            public void onHide(boolean isCancle, String picvcode) {
                Log.i(TAG, "onHide()=" + isCancle);
                dialogUpload.dismiss();

            }
        };
        if (dialogUpload == null) {
            dialogUpload = new ShowMusiciDialog(getActivity(), R.style.MyDialog, url, mOnSureClickListener);
            Window win = dialogUpload.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            win.setGravity(Gravity.RELATIVE_LAYOUT_DIRECTION | Gravity.CENTER);// 对话框的位置
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
        } else {
            Log.i(TAG, "dialogUpload!= null");
            dialogUpload.initShowData(url);
        }
        if (!((Activity) getActivity()).isFinishing() && dialogUpload != null)//如果不加这个代码,token android.os.BinderProxy@42abae08 is not valid; is your activity running?
        {
            dialogUpload.show();
            mRivMusicThumbPlay.setImageResource(R.drawable.u2);
        }
        dialogUpload.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mRivMusicThumbPlay.setImageResource(R.drawable.u2);
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
        mUPlayer.stop();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addMusic:
                mAddSelectMusic.setVisibility(View.VISIBLE);
                MyLog.i(TAG, "addMusic");
                break;
            case R.id.ivCancel:
                mAddSelectMusic.setVisibility(View.GONE);
                break;
            case R.id.btnLocalImport:
                //把扫描到的音乐赋值给list
                list.clear();
                for (int i = 0; i < 10; i++) {
                    Song mSong = new Song();
                    mSong.setPath("http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3");
                    mSong.setDuration(256);
                    mSong.setSinger("李宇春");
                    mSong.setSong("下个路口见吗？");
                    list.add(i, mSong);
                }
                mAdapter.notifyDataSetChanged();

                mBtnMusicKit.setTextColor(getContext().getColor(R.color.index_color));
                mBtnMusicKit.setBackgroundResource(R.drawable.seletor_buttom_music_nor);
                mBtnLocalImport.setTextColor(getContext().getColor(R.color.white));
                mBtnLocalImport.setBackgroundResource(R.drawable.seletor_buttom_music);
                break;
            case R.id.btnMusicKit:

                mBtnLocalImport.setTextColor(getContext().getColor(R.color.index_color));
                mBtnLocalImport.setBackgroundResource(R.drawable.seletor_buttom_music_nor);
                mBtnMusicKit.setTextColor(getContext().getColor(R.color.white));
                mBtnMusicKit.setBackgroundResource(R.drawable.seletor_buttom_music);

                break;
            default:
                break;
        }
    }


}
