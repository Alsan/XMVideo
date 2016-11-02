package cn.xm.xmvideoplayer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


import butterknife.Bind;
import butterknife.OnClick;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.adapter.FavoriteAdapter;
import cn.xm.xmvideoplayer.listener.OnFavoriteItemDeleteListener;
import cn.xm.xmvideoplayer.constant.DbConstant;
import cn.xm.xmvideoplayer.constant.UmengFieldConstant;
import cn.xm.xmvideoplayer.core.BaseSwipeBackActivity;
import cn.xm.xmvideoplayer.data.realm.DbFav;
import cn.xm.xmvideoplayer.entity.PageDetailInfo;

import cn.xm.xmvideoplayer.utils.DensityUtil;

import cn.xm.xmvideoplayer.utils.SnackbarUtil;
import io.realm.RealmResults;


public class act_favorite extends BaseSwipeBackActivity implements OnFavoriteItemDeleteListener {

    /**
     * adapter适配器
     */
    private FavoriteAdapter mAdapter;

    /**
     * 当前页数
     */
    private int currentpage = 1;

    /**
     * recycleview
     */
    @Bind(R.id.recycler_view)
    EasyRecyclerView mrecyclerView;

    @Bind(R.id.tv_fav_search)
    TextView tvfavsearch;

    @Bind(R.id.tv_favorite)
    TextView tv_favorite;

    /**
     * 上下文
     */
    private act_favorite mContext;
    /**
     * 数据库
     */
    private DbFav dbFav;
    private RealmResults<PageDetailInfo> results;

    private boolean isFirst = true;

    @Override
    public int getLayoutId() {
        return R.layout.act_favorite;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mContext = act_favorite.this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbFav == null) {
            dbFav = DbFav.Builder(mContext, DbConstant.DbFavrite);
        }
        getData();
        initRecycle();
    }

    @Override
    public String setUmengTag() {
        return "act_favorite";
    }

    /**
     * 初始化recycleview
     */
    private void initRecycle() {
        mAdapter = new FavoriteAdapter(mContext, results);
        mrecyclerView.setAdapter(mAdapter);
        mrecyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter.setOnFavoriteItemDeleteListener(this);
        //分割线只加载一次
        if (isFirst) {
            final SpaceDecoration itemDecoration = new SpaceDecoration(DensityUtil.dip2px(mContext, 5));//参数是距离宽度
            itemDecoration.setPaddingEdgeSide(false);//是否为左右2边添加padding.默认true.
            itemDecoration.setPaddingStart(true);//是否在给第一行的item添加上padding(不包含header).默认true.
            itemDecoration.setPaddingHeaderFooter(false);//是否对Header于Footer有效,默认false.
            mrecyclerView.addItemDecoration(itemDecoration);
            isFirst = false;
        }
    }

    /**
     * 获取数据，更新recycle
     */
    private void getData() {
        if (!dbFav.FindAllIsExit()) {//如果收藏夹为空
            //LogUitl.LogIMsg("收藏夹为空");
            tv_favorite.setVisibility(View.VISIBLE);
        } else {//更新数据
            tv_favorite.setVisibility(View.GONE);
            results = dbFav.FindAll();
        }
    }

    /**
     * 搜索按键点击事件
     *
     * @param view
     */
    @OnClick(R.id.tv_fav_search)
    public void click1(View view) {
        startActivity(new Intent(mContext, act_searchpage.class));
    }

    /**
     * 友盟取消收藏统计
     */
    private void umengFavoriteCancel(String title) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengFieldConstant.fieldfavorite, title);
        MobclickAgent.onEvent(act_favorite.this, UmengFieldConstant.btnfavoritecancel, map);
    }


    /**
     * 删除item的回调
     */
    @Override
    public void onItemDelete(String title, boolean isNull) {
        umengFavoriteCancel(title);//友盟记录取消收藏
        if (!isNull) {//如果收藏夹为空
            //LogUitl.LogIMsg("收藏夹为空");
            tv_favorite.setVisibility(View.VISIBLE);
        }
        SnackbarUtil.SnackBarShort(mrecyclerView, "取消收藏成功");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LogUitl.LogIMsg("onDestroy");
        if (dbFav != null) {
            dbFav.Close();
        }
    }
}
