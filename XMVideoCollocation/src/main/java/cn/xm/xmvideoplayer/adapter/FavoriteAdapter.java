package cn.xm.xmvideoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.constant.DbConstant;
import cn.xm.xmvideoplayer.data.realm.DbFav;
import cn.xm.xmvideoplayer.entity.PageDetailInfo;
import cn.xm.xmvideoplayer.listener.OnFavoriteItemDeleteListener;
import cn.xm.xmvideoplayer.utils.JumpActivityUtil;
import io.realm.OrderedRealmCollection;

/**
 * Created by gaohailong on 2016/5/17.
 */
public class FavoriteAdapter extends RecyclerSwipeAdapter<FavoriteHolder> {

    OnFavoriteItemDeleteListener onFavoriteItemDeleteListener;

    public void setOnFavoriteItemDeleteListener(OnFavoriteItemDeleteListener onFavoriteItemDeleteListener) {
        this.onFavoriteItemDeleteListener = onFavoriteItemDeleteListener;
    }

    private Context mContext;
    private OrderedRealmCollection<PageDetailInfo> mDataset;

    public FavoriteAdapter(Context context, OrderedRealmCollection<PageDetailInfo> data) {
        this.mContext = context;
        this.mDataset = data;
    }


    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteHolder viewHolder, final int position) {
        //更新数据
        viewHolder.setData(mDataset.get(position), mContext);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
            }
        });
        viewHolder.tv_item_swipe_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbFav dbFav = DbFav.Builder(mContext, DbConstant.DbFavrite);
                String title = mDataset.get(position).getTitle();//存储删除前的标题，回调用
                //删除数据
                dbFav.DeleteItem(mDataset.get(position).getUrl());
                boolean isNull = dbFav.FindAllIsExit();//删除后数据是否为null,回调用
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                dbFav.Close();
                //回调
                onFavoriteItemDeleteListener.onItemDelete(title, isNull);
            }
        });
        viewHolder.llitemcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到电视剧
                JumpActivityUtil.JumpPageDetail2(position, mContext, mDataset);
            }
        });
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) {
            return 0;
        }
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
