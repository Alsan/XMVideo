package cn.xm.xmvideoplayer.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.adapter.TabHomePagerAdaper;
import cn.xm.xmvideoplayer.constant.UmengFieldConstant;
import cn.xm.xmvideoplayer.core.BaseFragment;
import cn.xm.xmvideoplayer.ui.activity.act_favorite;
import cn.xm.xmvideoplayer.ui.activity.act_searchpage;
import cn.xm.xmvideoplayer.view.HomeRecycleView1;
import cn.xm.xmvideoplayer.view.HomeRecycleView2;
import cn.xm.xmvideoplayer.view.HomeRecycleView3;
import cn.xm.xmvideoplayer.view.HomeRecycleView4;
import cn.xm.xmvideoplayer.view.HomeRecycleView5;

/**
 * homefra
 */
public class fra_home extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ll_search)
    LinearLayout llSearch;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private String mParam1;
    private String mParam2;

    private List<String> listTilte = new ArrayList<>();
    private List<View> listView = new ArrayList<>();
    private HomeRecycleView1 homeRecycleView1;
    private HomeRecycleView2 homeRecycleView2;
    private HomeRecycleView3 homeRecycleView3;
    private HomeRecycleView4 homeRecycleView4;
    private HomeRecycleView5 homeRecycleView5;


    public static fra_home newInstance(String param1, String param2) {
        fra_home fragment = new fra_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //初始化menu
        setHasOptionsMenu(true);

    }


    @Override
    public int getLayoutId() {
        return R.layout.fra_home;
    }

    @Override
    public void initViews() {
        //toolsbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
 /*       toolbar.setTitleTextColor(getResources().getColor(R.color.black_90));
        //toolbar返回键
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.black_90), PorterDuff.Mode.SRC_ATOP);
        activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        //viewpager
        listTilte.add(getString(R.string.lastupdate));
        listTilte.add(getString(R.string.film));
        listTilte.add(getString(R.string.tvplay));
        listTilte.add(getString(R.string.cartoon));
        listTilte.add(getString(R.string.variety));
        homeRecycleView1 = HomeRecycleView1.Builder();
        listView.add(homeRecycleView1.createView(activity));
        homeRecycleView2 = HomeRecycleView2.Builder();
        listView.add(homeRecycleView2.createView(activity));
        homeRecycleView3 = HomeRecycleView3.Builder();
        listView.add(homeRecycleView3.createView(activity));
        homeRecycleView4 = HomeRecycleView4.Builder();
        listView.add(homeRecycleView4.createView(activity));
        homeRecycleView5 = HomeRecycleView5.Builder();
        listView.add(homeRecycleView5.createView(activity));

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new TabHomePagerAdaper(listView, listTilte));

        //TabLayout
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_grade:
                MobclickAgent.onEvent(getActivity(), UmengFieldConstant.btnHomeFavorite);
                startActivity(new Intent(getActivity(), act_favorite.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 首页搜索
     *
     * @param view
     */
    @OnClick(R.id.ll_search)
    public void click1(View view) {
        MobclickAgent.onEvent(getActivity(), UmengFieldConstant.btnHomeSearch);
        startActivity(new Intent(getActivity(), act_searchpage.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeRecycleView1.destroy();
        homeRecycleView2.destroy();
        homeRecycleView3.destroy();
        homeRecycleView4.destroy();
        homeRecycleView5.destroy();
    }

    @Override
    public String setUmengTag() {
        return "Fragment_home";
    }
}
