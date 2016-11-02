package cn.xm.xmvideoplayer.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;

import java.io.File;
import java.util.List;

import butterknife.ButterKnife;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.adapter.TabHomeAdapter;
import cn.xm.xmvideoplayer.constant.FieldConstant;
import cn.xm.xmvideoplayer.constant.IntenConstant;
import cn.xm.xmvideoplayer.data.jsoup.JsoupApi;
import cn.xm.xmvideoplayer.entity.PageInfo;
import cn.xm.xmvideoplayer.ui.activity.act_seasondetail;
import cn.xm.xmvideoplayer.utils.DensityUtil;
import cn.xm.xmvideoplayer.utils.JumpActivityUtil;

/**
 * 作者：ximencx on 2016/6/16 16:16
 * 邮箱：454366460@qq.com
 */
public class HomeRecycleView2 implements RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String type = JsoupApi.typeFilm;
    private Handler handler = new Handler();
    private TabHomeAdapter mAdapter;

    /**
     * 当前页数
     */
    private int currentpage = 1;
    private EasyRecyclerView mrecyclerView;
    private Thread thread;
    private File cacheDir;

    public static HomeRecycleView2 Builder() {
        return new HomeRecycleView2();
    }

    /**
     * 初始化Recycleview
     */
    public EasyRecyclerView createView(final Activity activity) {
        cacheDir = activity.getCacheDir();
        mAdapter = new TabHomeAdapter(activity);

        final View view = activity.getLayoutInflater().inflate(R.layout.incd_content_recycleview, null);
        mrecyclerView = ButterKnife.findById(view, R.id.recycler_view);
        //
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.setAdapterWithProgress(mAdapter);
        //
        SpaceDecoration itemDecoration = new SpaceDecoration(DensityUtil.dip2px(activity, 5));//参数是距离宽度
        itemDecoration.setPaddingEdgeSide(false);//是否为左右2边添加padding.默认true.
        itemDecoration.setPaddingStart(true);//是否在给第一行的item添加上padding(不包含header).默认true.
        itemDecoration.setPaddingHeaderFooter(false);//是否对Header于Footer有效,默认false.
        mrecyclerView.addItemDecoration(itemDecoration);
        //
        mAdapter.setMore(R.layout.load_more_layout, this);
        mAdapter.setNoMore(R.layout.no_more_layout);
        mAdapter.setError(R.layout.error_layout);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //跳转到电视剧
                JumpActivityUtil.JumpPageDetail(position, activity, mAdapter);
            }
        });
        onRefresh();
        mrecyclerView.setRefreshListener(this);
        return mrecyclerView;
    }

    /**
     * 获取数据
     *
     * @param page 页码
     * @param type 类型
     */
    private void getData(final boolean isclear, final int page, final String type) {
        //未打开wifi或连接有问题
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<PageInfo> pageInfos = JsoupApi.NewInstans(cacheDir).GetPage(page, type);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pageInfos == null) {//未打开wifi或连接有问题
                            mAdapter.addAll(pageInfos);
                            mAdapter.pauseMore();
                            return;
                        } else {
                            if (isclear) {
                                if (mAdapter.getCount() > 0) {
                                    mAdapter.clear();
                                }
                            }
                            mAdapter.addAll(pageInfos);
                            if (currentpage>= FieldConstant.MaxPage) {
                                mAdapter.stopMore();
                            }
                        }
                    }
                });
            }
        });
        thread.start();
    }


    @Override
    public void onLoadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentpage++;
                getData(false,currentpage, type);
            }
        });
    }

    @Override
    public void onRefresh() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                getData(true,1, type);
            }
        });
    }

    public void destroy() {
        handler.removeCallbacksAndMessages(null);
        if (thread!=null&&thread.isAlive()){
            thread.interrupt();
        }
    }
}
