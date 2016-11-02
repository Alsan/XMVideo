package cn.xm.xmvideoplayer.utils;

import android.content.Context;
import android.content.Intent;

import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import cn.xm.xmvideoplayer.adapter.FavoriteHolder;
import cn.xm.xmvideoplayer.adapter.TabHomeAdapter;
import cn.xm.xmvideoplayer.constant.IntenConstant;
import cn.xm.xmvideoplayer.entity.PageDetailInfo;
import cn.xm.xmvideoplayer.entity.PageInfo;
import cn.xm.xmvideoplayer.ui.activity.act_seasondetail;
import io.realm.OrderedRealmCollection;

/**
 * Created by WANG on 2016/7/31.
 */
public class JumpActivityUtil {

    /**
     * 跳转到页面详情PageInfo(搜索，首页)
     *
     * @param position       adapter的position
     * @param packageContext 上下文
     * @param mAdapter       adapter
     */
    public static <E extends RecyclerArrayAdapter<PageInfo>> void JumpPageDetail(int position, Context packageContext, E mAdapter) {
        Intent intent = new Intent(packageContext, act_seasondetail.class);
        intent.putExtra(IntenConstant.pagedetailurl, mAdapter.getItem(position).getAhref());
        intent.putExtra(IntenConstant.pageInfo, mAdapter.getItem(position));
        packageContext.startActivity(intent);
    }


    /**
     * 跳转到页面详情PageDetailInfo(收藏页面)
     *
     * @param position       adapter的position
     * @param packageContext 上下文
     * @param mAdapter       adapter
     */
    public static <E extends OrderedRealmCollection<PageDetailInfo>> void JumpPageDetail2(int position, Context packageContext, E mAdapter) {
        Intent intent = new Intent(packageContext, act_seasondetail.class);
        intent.putExtra(IntenConstant.pagedetailurl, mAdapter.get(position).getPageInfo().getAhref());
        intent.putExtra(IntenConstant.pageInfo, mAdapter.get(position).getPageInfo());
        packageContext.startActivity(intent);
    }
}
