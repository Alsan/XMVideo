package cn.xm.xmvideoplayer.core;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * 作者：ximencx on 2016/6/12 20:58
 * 邮箱：454366460@qq.com
 */
public abstract class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置布局内容
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        ButterKnife.bind(this);
        //适配4.4状态栏
       // StatusBarCompatUtil.compat(this);
        //初始化控件
        initViews(savedInstanceState);
        //初始化ToolBar
        initToolBar();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(setUmengTag());
        MobclickAgent.onResume(this);//统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(setUmengTag());
        MobclickAgent.onPause(this);
    }

    /**
     * 设置umengtag
     *
     * @return 返回tag值
     */
    public abstract String setUmengTag();

    public abstract int getLayoutId();

    public abstract void initViews(Bundle savedInstanceState);

    public abstract void initToolBar();
}
