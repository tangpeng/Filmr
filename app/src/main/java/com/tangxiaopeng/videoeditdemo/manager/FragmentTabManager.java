package com.tangxiaopeng.videoeditdemo.manager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tangxiaopeng.videoeditdemo.utils.MyLog;

import java.util.List;

/**
 * fragmentManager管理类
 */
public class FragmentTabManager {
    private final String TAG = "FragmentTabManager";

    private List<Fragment> fragments;

    private RadioGroup radioGroup;

    private FragmentManager fragmentManager;

    private int fragment_containerId;

    private Context context;

    private int currentTab; // 当前Tab页面索引

    public FragmentTabManager(List<Fragment> fragments, RadioGroup radioGroup,
                              FragmentManager fragmentManager, int fragment_containerId,
                              Context context) {

        this.fragments = fragments;
        this.radioGroup = radioGroup;
        this.fragmentManager = fragmentManager;
        this.fragment_containerId = fragment_containerId;
        this.context = context;
        //初始化
        this.fragmentManager.beginTransaction()
                .add(this.fragment_containerId, this.fragments.get(0))
                .commit();
    }


    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }

    /* 管理tab页 */
    public void managerTab() {
        MyLog.i(TAG,"radioGroup="+radioGroup);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setEnabled(true);
            radioButton.setTag(i);
            final int pos = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RadioButton radioButton1 = (RadioButton) v;
                    Integer position = (Integer) radioButton1.getTag();

                    Fragment fragment = fragments.get(position);
                    FragmentTransaction ft = obtainFragmentTransaction();
                    getCurrentFragment().onStop(); // 暂停当前tab
                    if (fragment.isAdded()) {
                        fragment.onResume(); // 启动目标tab的onResume()
                    } else {
                        ft.add(fragment_containerId, fragment);
                    }
                    mOnResultener.setInt(pos);
                    showTab(pos); // 显示目标tab,
                    ft.commit();
                }
            });
        }
    }

    ;

    /**
     * 切换tab
     *
     * @param idx
     */
    private void showTab(int idx) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = obtainFragmentTransaction();
            if (idx == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    public void selectButton(int index) {
        ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        Fragment fragment = fragments.get(index);
        FragmentTransaction ft = obtainFragmentTransaction();
        getCurrentFragment().onStop(); // 暂停当前tab
        if (fragment.isAdded()) {
            fragment.onResume(); // 启动目标tab的onResume()
        } else {
            ft.add(fragment_containerId, fragment);
        }
        MyLog.i(TAG, "index=" + index);
        showTab(index); // 显示目标tab
        ft.commitAllowingStateLoss();
    }

    /**
     * @return
     */
    private FragmentTransaction obtainFragmentTransaction() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        return ft;
    }


    public void getInitCount() {
        ((RadioButton) this.radioGroup.getChildAt(0)).setChecked(true);//初始化
        MyLog.i(TAG, "FragmentTabManager.beginTransaction");
    }


    /**
     * 回调到activity
     */
    public OnResultener mOnResultener;
    public void setResulter(OnResultener mOnResultener) {
        this.mOnResultener = mOnResultener;
    }
    public interface OnResultener {
        public void setInt(int position);
    }
}
