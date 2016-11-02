package cn.xm.xmvideoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.entity.PageDetailInfo;


/**
 * Created by gaohailong on 2016/5/17.
 */
public class FavoriteHolder extends BaseViewHolder {

    public TextView tv_title;
    public TextView tv_title2;
    public TextView tv_title3;
    public TextView tv_title4;
    public TextView tv_item_swipe_delete;
    public ImageView iv_cover;
    public LinearLayout llitemcontent;
    public SwipeLayout swipeLayout;


    public FavoriteHolder(View itemView) {
        super(itemView);
        tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        tv_title2 = (TextView) itemView.findViewById(R.id.tv_title2);
        tv_title3 = (TextView) itemView.findViewById(R.id.tv_title3);
        tv_title4 = (TextView) itemView.findViewById(R.id.tv_title4);
        iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
        tv_item_swipe_delete = (TextView) itemView.findViewById(R.id.tv_item_swipe_delete);
        llitemcontent = (LinearLayout) itemView.findViewById(R.id.ll_item_content);
        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
    }

    public void setData(PageDetailInfo data, Context mContext) {
        //提取标题
        Matcher matcher = Pattern.compile("《(.*?)》").matcher(data.getPageInfo().getTitle());
        if (matcher.find()) {
            tv_title.setText(matcher.group(1));
        }
        tv_title2.setText(data.getPageInfo().getYear() + "  " + data.getPageInfo().getScore() + "分  " + data.getPageInfo().getType() + "  " + data.getPageInfo().getAddr());
        tv_title3.setText("主演:" + data.getPageInfo().getActor());
        tv_title4.setText("发布时间:" + data.getPageInfo().getUpdatetime());
        Glide.with(mContext)
                .load(data.getCover())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .fitCenter()
                .into(iv_cover);
    }

}
