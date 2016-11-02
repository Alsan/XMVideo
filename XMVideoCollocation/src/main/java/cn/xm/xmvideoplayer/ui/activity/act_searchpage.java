package cn.xm.xmvideoplayer.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.adapter.SearchHisAdapter;
import cn.xm.xmvideoplayer.adapter.TabHomeAdapter;
import cn.xm.xmvideoplayer.constant.DbConstant;
import cn.xm.xmvideoplayer.constant.UmengFieldConstant;
import cn.xm.xmvideoplayer.core.BaseSwipeBackActivity;
import cn.xm.xmvideoplayer.data.jsoup.JsoupApi;
import cn.xm.xmvideoplayer.data.realm.DbSearchHis;
import cn.xm.xmvideoplayer.entity.PageInfo;
import cn.xm.xmvideoplayer.entity.SearchHistroy;
import cn.xm.xmvideoplayer.utils.DensityUtil;
import cn.xm.xmvideoplayer.utils.JumpActivityUtil;
import cn.xm.xmvideoplayer.utils.SnackbarUtil;
import io.realm.RealmResults;

public class act_searchpage extends BaseSwipeBackActivity implements RecyclerArrayAdapter.OnLoadMoreListener {

    @Bind(R.id.et_search)
    EditText etsearch;

    @Bind(R.id.tv_clearhis)
    TextView tvclearhis;

    @Bind(R.id.tv_search)
    TextView tesearch;

    @Bind(R.id.ll_history)
    LinearLayout llhistory;

    @Bind(R.id.ll_result)
    LinearLayout llresult;

    @Bind(R.id.recycler_his)
    EasyRecyclerView recyclerhis;

    @Bind(R.id.recycler_result)
    EasyRecyclerView mrecyclerView;

    /**
     * 数据库
     */
    private DbSearchHis dbSearchHis;
    /**
     * 搜索记录（数据库）
     */
    private SearchHisAdapter searchHisAdapter;
    /**
     * handler
     */
    private Handler handler = new Handler();
    /**
     * adapter（网络）
     */
    private TabHomeAdapter mAdapter;
    /**
     * intent关键字
     */
    private String keyword;
    /**
     * 当前页数
     */
    private int currentpage = 1;
    /**
     * 搜索功能线程
     */
    private Thread thread;

    /**
     * 首次查询
     */
    private boolean isFirst = true;

    @Override
    public int getLayoutId() {
        return R.layout.act_search_page;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initSearchHis();
    }

    /**
     * 搜索记录
     */
    private void initSearchHis() {
        //隐藏搜索结果
        llresult.setVisibility(View.INVISIBLE);
        //搜索记录显示
        if (dbSearchHis == null) {
            dbSearchHis = DbSearchHis.Builder(this, DbConstant.DbSearchHis);
        }
        if (dbSearchHis.FindIsAllExit()) {//数据存在，显示
            llhistory.setVisibility(View.VISIBLE);
            //搜索记录
            initRecycleSearchHis();
        } else {//数据不存在，
            llhistory.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 搜索记录
     */
    private void initRecycleSearchHis() {
        RealmResults<SearchHistroy> searchHistroys = dbSearchHis.FindAll();
        ArrayList<String> datalistSearchHis = new ArrayList<>();
        for (SearchHistroy obj : searchHistroys) {
            datalistSearchHis.add(obj.getField());
        }
        //倒序
        Collections.reverse(datalistSearchHis);
        //recycle
        searchHisAdapter = new SearchHisAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerhis.setLayoutManager(linearLayoutManager);
        recyclerhis.setAdapter(searchHisAdapter);
        //添加数据
        searchHisAdapter.addAll(datalistSearchHis);
        searchHisAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String field = searchHisAdapter.getAllData().get(position).toString();
                //et设置text，启动搜索
                etsearch.setText(field);
                etsearch.setSelection(field.length());
                searchAction();
            }
        });
    }

    /**
     * 取消键
     *
     * @param view
     */
    @OnClick(R.id.tv_search)
    public void click1(View view) {
        finish();
    }

    /**
     * 键盘确定键搜索
     *
     * @return
     */
    @OnEditorAction(R.id.et_search)
    public boolean OnEditorAction1() {
        return searchAction();
    }

    /**
     * 搜索按键
     */
    private boolean searchAction() {
        //页码重置
        currentpage = 1;
        String filed = etsearch.getText().toString();
        //搜索不为空
        if (filed.length() <= 0) {
            SnackbarUtil.SnackBarShort(recyclerhis, "请输入关键字");
            return false;
        }
        //隐藏历史记录，显示搜索布局，关键词加入数据库
        llhistory.setVisibility(View.INVISIBLE);
        llresult.setVisibility(View.VISIBLE);
        if (dbSearchHis.DeleteItem(filed)) {//不存在
            dbSearchHis.Insert(new SearchHistroy(filed));
        }
        //访问网络，获取结果
        initRecycleSearchList();
        return true;
    }

    /**
     * 搜索结果
     */
    private void initRecycleSearchList() {
        String filed = etsearch.getText().toString();
        //搜索不为空
        if (filed.length() <= 0) {
            SnackbarUtil.SnackBarShort(recyclerhis, "请输入关键字");
            return;
        }
        //umeng统计搜索关键词
        UmengSearch(filed);
        //设置recycleview
        mAdapter = new TabHomeAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mrecyclerView.setLayoutManager(linearLayoutManager);
        //分割线
        if (isFirst) {
            SpaceDecoration itemDecoration = new SpaceDecoration(DensityUtil.dip2px(this, 5));//参数是距离宽度
            itemDecoration.setPaddingEdgeSide(false);//是否为左右2边添加padding.默认true.
            itemDecoration.setPaddingStart(true);//是否在给第一行的item添加上padding(不包含header).默认true.
            itemDecoration.setPaddingHeaderFooter(false);//是否对Header于Footer有效,默认false.
            mrecyclerView.addItemDecoration(itemDecoration);
            isFirst = false;
        }
        //
        mAdapter.setMore(R.layout.load_more_layout, this);
        mAdapter.setError(R.layout.error_layout);
        mAdapter.setNoMore(R.layout.no_more_layout);
        mrecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //跳转到电视剧
                JumpActivityUtil.JumpPageDetail(position, act_searchpage.this, act_searchpage.this.mAdapter);
            }
        });
        //填充数据
        keyword = filed;
        //访问网络
        startSearch();
    }

    /**
     * 友盟统计搜索关键词
     *
     * @param filed 搜索关键词
     */
    private void UmengSearch(String filed) {
        HashMap<String, String> map = new HashMap<>();
        map.put(UmengFieldConstant.fieldsearch, filed);
        MobclickAgent.onEvent(act_searchpage.this, UmengFieldConstant.btnsearch, map);
    }


    /**
     * 搜索框监听
     */
    @OnTextChanged(value = R.id.et_search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void textchange1() {
        String filed = etsearch.getText().toString();
        if (filed.length() <= 0) {//输入框0字符，显示搜索记录
            initSearchHis();
        }
    }

    /**
     * 清空记录
     *
     * @param view
     */
    @OnClick(R.id.tv_clearhis)
    public void click2(View view) {
        if (!dbSearchHis.FindIsAllExit()) {
            return;
        }
        if (dbSearchHis.DeleteAll()) {//删除成功，刷新Recycleview
            searchHisAdapter.clear();
            searchHisAdapter.notifyDataSetChanged();
            SnackbarUtil.SnackBarShort(recyclerhis, "记录已清空");
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentpage++;
                getData(false, currentpage, keyword);
            }
        });
    }

    /**
     * 设置搜索
     */
    private void startSearch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getData(true, 1, keyword);
            }
        });
    }

    /**
     * 获取数据
     *
     * @param page  页码
     * @param filed 关键词
     */
    private void getData(final boolean isclear, final int page, final String filed) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<PageInfo> pageInfos = JsoupApi.NewInstans(getCacheDir()).GetPageSearch(page, filed);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pageInfos == null) {//未打开wifi或连接有问题
                            mAdapter.pauseMore();
                            return;
                        }
                        //添加数据
                        if (isclear) {
                            if (mAdapter.getCount() > 0) {
                                mAdapter.clear();
                            }
                        }
                        mAdapter.addAll(pageInfos);
                        //需要添加数据后面
                        if (pageInfos.size() > 0) {
                            if (currentpage >= Integer.valueOf(pageInfos.get(0).getPage())) {
                                mAdapter.stopMore();
                            }
                        }
                        //Log.i("msg", pageInfos.size() + "  page:" + page + "  " + pageInfos.toString());
                    }
                });
            }
        });
        thread.start();
    }


    @Override
    public String setUmengTag() {
        return "act_searchpage";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbSearchHis != null) {
            dbSearchHis.Close();
        }
        handler.removeCallbacksAndMessages(null);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
