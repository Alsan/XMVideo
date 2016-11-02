package cn.xm.xmvideoplayer.core;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * 作者：ximencx on 2016/6/12 20:58
 * 邮箱：454366460@qq.com
 */
public abstract class BaseFragment extends Fragment {
    private View rootView;
    private String UmengTag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initViews();
        UmengTag = setUmengTag();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengTag); //统计页面，"HomeScreen"为页面名称，可自定义
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengTag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 设置umengtag
     *
     * @return 返回tag值
     */
    public abstract String setUmengTag();

    public abstract int getLayoutId();

    public abstract void initViews();
}
