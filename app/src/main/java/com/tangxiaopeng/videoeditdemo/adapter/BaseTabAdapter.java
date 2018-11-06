package com.tangxiaopeng.videoeditdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

/**
 * ProjectName:YiSai
 * Date:2017/11/7 18:24
 * Created by JY
 * TabLayout+viewPager使用的适配器
 */
public class BaseTabAdapter extends FragmentPagerAdapter {

    private static final String TAG = "BaseTabAdapter";

    private List<Fragment> mFragments;  //fragment列表
    private FragmentManager mFragmentManager;

    private String[] mTitles; //tab名的列表

    public BaseTabAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
        mFragmentManager = fm;
    }

    public BaseTabAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles) {
        super(fm);
        this.mFragments = fragments;
        this.mTitles = titles;
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles == null || mTitles.length == 0){
            return null;
        }
        return mTitles[position % mTitles.length];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

    /**
     * 强制刷新fragment数据
     * @param fragments
     */
    public void setFragments(List<Fragment> fragments) {
        if (mFragments != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            for (Fragment f : mFragments) {
                ft.remove(f);
            }
            ft.commit();
            mFragmentManager.executePendingTransactions();
        }
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    /**
     * POSITION_NONE意思是没有找到child要求重新加载。
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
