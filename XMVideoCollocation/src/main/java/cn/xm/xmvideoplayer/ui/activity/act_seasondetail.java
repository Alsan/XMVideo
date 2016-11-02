package cn.xm.xmvideoplayer.ui.activity;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.adapter.SeasonDetailAdapter;
import cn.xm.xmvideoplayer.constant.AdConstant;
import cn.xm.xmvideoplayer.constant.DbConstant;
import cn.xm.xmvideoplayer.constant.FieldConstant;
import cn.xm.xmvideoplayer.constant.IntenConstant;
import cn.xm.xmvideoplayer.constant.UmengFieldConstant;
import cn.xm.xmvideoplayer.core.BaseSwipeBackActivity;
import cn.xm.xmvideoplayer.data.jsoup.JsoupApi;
import cn.xm.xmvideoplayer.data.realm.DbFav;
import cn.xm.xmvideoplayer.entity.PageDetailInfo;
import cn.xm.xmvideoplayer.entity.PageInfo;
import cn.xm.xmvideoplayer.utils.CheckAppUtil;
import cn.xm.xmvideoplayer.utils.DensityUtil;
import cn.xm.xmvideoplayer.utils.SnackbarUtil;

/**
 * 电视剧详情
 */
public class act_seasondetail extends BaseSwipeBackActivity implements RecyclerArrayAdapter.OnLoadMoreListener {

    @Bind(R.id.detail_image)
    ImageView detailImage;

    @Bind(R.id.rl_progress)
    RelativeLayout rlprogress;

    @Bind(R.id.toolbar)
    Toolbar mtoolbar;

    @Bind(R.id.coll_toolbar_layout)
    CollapsingToolbarLayout mcollToolbarLayout;

    @Bind(R.id.recycler_view)
    EasyRecyclerView mrecyclerView;

    @Bind(R.id.pb_progress)
    ProgressBar pbprogress;

    @Bind(R.id.tv_progress)
    TextView tvprogress;

    /**
     * handler
     */
    private Handler mhandler = new Handler();

    /**
     * actionbar
     */
    private ActionBar mActionBar;
    /**
     * 下载链接adapter
     */
    private SeasonDetailAdapter mAdapter;
    /**
     * intent传入的链接
     */
    private String pageDetailurl;
    /**
     * 简介内容
     */
    private TextView tv_content;
    /**
     * 演员上映日期
     */
    private TextView tv_actor;
    /**
     * 展开收起简介
     */
    private TextView tv_expend;

    /**
     * 收藏
     */
    private TextView tv_fav;

    /**
     * 访问线程
     */
    private Thread thread1;

    /**
     * 数据库（收藏）
     */
    private DbFav dbFav;

    /**
     * intent获取对象
     */
    private PageInfo pageInfo;

    @Override
    public int getLayoutId() {
        return R.layout.act_seasondetail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        pageDetailurl = getIntent().getStringExtra(IntenConstant.pagedetailurl);
        pageInfo = getIntent().getParcelableExtra(IntenConstant.pageInfo);
        //toolbar
        inittoolbar();
        //recycleview
        initrecycle();
        //getdata
        getDetaildata();
    }

    /**
     * 广告sdk
     */
    InterstitialAD iad;

    /**
     * 获取ad对象
     *
     * @return
     */
    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(this, AdConstant.APPID, AdConstant.InterteristalPosID);
        }
        return iad;
    }

    /**
     * 展示ad
     */
    private void showAD() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
            }

            @Override
            public void onADReceive() {
                Log.i("AD_DEMO", "onADReceive");
                iad.show();
            }
        });
        iad.loadAD();
    }

    /**
     * 初始化recycle
     */
    private void initrecycle() {
        //
        mAdapter = new SeasonDetailAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        //
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return initheadview();
            }

            @Override
            public void onBindView(View headerView) {

            }
        });
        mAdapter.setMore(R.layout.load_more_layout, this);
        mAdapter.setNoMore(R.layout.no_more_layout);
        mAdapter.setError(R.layout.error_layout);
        mrecyclerView.setAdapterWithProgress(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {//单击下载
            @Override
            public void onItemClick(int position) {
                //如果手机包含迅雷和uc,则提示下载
                if (CheckAppUtil.isAvilible(act_seasondetail.this, FieldConstant.PackgetUC) || CheckAppUtil.isAvilible(act_seasondetail.this, FieldConstant.PackgetXL)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAdapter.getItem(position)));
                        intent.addCategory("android.intent.category.DEFAULT");
                        startActivity(intent);
                        Snackbar.make(mrecyclerView, "启动下载", Snackbar.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();//兼容某些机型崩溃
                        Snackbar.make(mrecyclerView, "下载需要:手机迅雷或UC浏览器", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(mrecyclerView, "下载需要:手机迅雷或UC浏览器", Snackbar.LENGTH_LONG).show();
                }

            }
        });
        mAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {//长按复制
            @Override
            public boolean onItemClick(int position) {
                String label = "复制的链接";
                String item = mAdapter.getItem(position);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(new ClipData(new ClipDescription(label, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}), new ClipData.Item(item)));
                Snackbar.make(mrecyclerView, "已复制链接:" + item, Snackbar.LENGTH_LONG).show();
                return true;
            }
        });
    }

    /**
     * 初始化toolbar
     */
    private void inittoolbar() {
        // 初始化ToolBar
        setSupportActionBar(mtoolbar);
        mActionBar = getSupportActionBar();
        //返回键
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        //upArrow.setColorFilter(getResources().getColor(R.color.black_90), PorterDuff.Mode.SRC_ATOP);
        mtoolbar.setNavigationIcon(upArrow);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*//设置title颜色
        //mcollToolbarLayout.setTitle("title");
        mcollToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.black_90));
        mcollToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black_90));
        mtoolbar.setTitleTextColor(getResources().getColor(R.color.black_90));*/
        mcollToolbarLayout.setExpandedTitleMarginEnd(DensityUtil.dip2px(this, 12.0f));
        mcollToolbarLayout.setExpandedTitleMarginStart(DensityUtil.dip2px(this, 12.0f));
    }

    /**
     * 获取网络数据
     */
    private void getDetaildata() {
        //初始化等待信息
        if (pbprogress != null && tvprogress != null) {//显示等待
            pbprogress.setVisibility(View.VISIBLE);
            tvprogress.setVisibility(View.GONE);
        }
        //访问网络
        thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                final PageDetailInfo pageDetailInfo = JsoupApi.NewInstans(getCacheDir()).GetPageDetail(pageDetailurl);
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pageDetailInfo == null) {//显示网络异常
                            if (pbprogress != null && tvprogress != null) {
                                pbprogress.setVisibility(View.GONE);
                                tvprogress.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        updataview(pageDetailInfo);
                        for (List<String> list : pageDetailInfo.getDownlist()) {
                            mAdapter.addAll(list);
                            mAdapter.stopMore();
                        }
                        if (pageDetailInfo.getDownlist().size() == 0) {
                            mAdapter.pauseMore();
                        }
                    }
                });
            }
        });
        thread1.start();
    }

    /**
     * 更新view
     */
    private void updataview(PageDetailInfo pageDetailInfo) {
        //取消progressbar
        if (rlprogress != null) {
            rlprogress.setVisibility(View.GONE);
            //显示插屏广告
            //showAD();
        }
        //header
        updateheaer(pageDetailInfo, pageDetailInfo.getSmalltext(), pageDetailInfo.getAlltext(), pageDetailInfo.getActor());
        //appbar
        if (pageDetailInfo.getTitle() != null && mcollToolbarLayout != null) {
            mcollToolbarLayout.setTitle(pageDetailInfo.getTitle());
        }
        //cover
        if (detailImage != null) {
            Glide.with(this)
                    .load(pageDetailInfo.getCover())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .crossFade()
                    .into(detailImage);
        }
    }

    /**
     * 初始化headerview
     *
     * @return
     */
    private View initheadview() {
        View view = getLayoutInflater().inflate(R.layout.item_seasonlist_header, null);
        tv_content = ButterKnife.findById(view, R.id.tv_content);
        tv_actor = ButterKnife.findById(view, R.id.tv_actor);
        tv_expend = ButterKnife.findById(view, R.id.tv_expend);
        tv_fav = ButterKnife.findById(view, R.id.tv_fav);
        return view;
    }

    /**
     * 更新headerview数据
     *
     * @param smalltext 部分简介
     * @param alltext   所有简介
     * @param actor     演员等信息
     */
    private void updateheaer(final PageDetailInfo pageDetailInfo, final String smalltext, final String alltext, String actor) {
        if (tv_content != null) {
            tv_content.setText(Html.fromHtml(smalltext));
        }
        if (tv_actor != null) {
            tv_actor.setText(Html.fromHtml(actor));
        }
        if (tv_expend != null && tv_content != null) {
            //展开收缩简介
            tv_expend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getResources().getString(R.string.textcontent3).equals(tv_expend.getText())) {//展开全部
                        tv_expend.setText(getResources().getString(R.string.textcontent4));
                        tv_content.setText(Html.fromHtml(alltext));
                    } else if (getResources().getString(R.string.textcontent4).equals(tv_expend.getText())) {//收起简介
                        tv_expend.setText(getResources().getString(R.string.textcontent3));
                        tv_content.setText(Html.fromHtml(smalltext));
                    }
                }
            });
        }
        //收藏状态
        dbFav = DbFav.Builder(this, DbConstant.DbFavrite);
        if (tv_fav != null) {
            if (dbFav.FindItemIsExit(pageDetailurl)) {//已收藏
                tv_fav.setText(getResources().getString(R.string.textfav2));
            } else {//未收藏
                tv_fav.setText(getResources().getString(R.string.textfav1));
            }
            tv_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbFav.FindItemIsExit(pageDetailurl)) {//已收藏
                        if (dbFav.DeleteItem(pageDetailurl)) {
                            //umeng统计收藏
                            umengFavoriteCancel();
                            Snackbar.make(mrecyclerView, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                            tv_fav.setText(getResources().getString(R.string.textfav1));
                        } else {
                            Snackbar.make(mrecyclerView, "取消收藏失败", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {//未收藏
                        if (dbFav.Insert(pageDetailInfo, pageInfo)) {
                            //umeng统计收藏
                            umengFavoriteAdd();
                            Snackbar.make(mrecyclerView, "添加收藏成功", Snackbar.LENGTH_SHORT).show();
                            tv_fav.setText(getResources().getString(R.string.textfav2));
                        } else {
                            Snackbar.make(mrecyclerView, "添加收藏失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }

    /**
     * 友盟添加收藏统计
     */
    private void umengFavoriteAdd() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengFieldConstant.fieldfavorite, pageInfo.getTitle());
        MobclickAgent.onEvent(act_seasondetail.this, UmengFieldConstant.btnfavoriteadd, map);
    }

    /**
     * 友盟取消收藏统计
     */
    private void umengFavoriteCancel() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengFieldConstant.fieldfavorite, pageInfo.getTitle());
        MobclickAgent.onEvent(act_seasondetail.this, UmengFieldConstant.btnfavoritecancel, map);
    }

    /**
     * 点击重新加载,重新获取数据
     */
    @OnClick(R.id.tv_progress)
    public void onclick1() {
        getDetaildata();
        SnackbarUtil.SnackBarShort(mrecyclerView, "努力加载中...");
    }

    @Override
    public void onLoadMore() {
        return;
    }

    @Override
    public String setUmengTag() {
        return "act_seasondetail";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbFav != null) {
            dbFav.Close();
        }
        if (mhandler != null) {
            mhandler.removeCallbacksAndMessages(null);
        }
        if (thread1.isAlive()) {
            thread1.interrupt();
        }
        if (iad != null) {
            iad.destory();
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            switch (id) {
                case R.id.action_history:
                    break;
            }
            return super.onOptionsItemSelected(item);
        }*/
}
