package cn.xm.xmvideoplayer.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.entity.PageInfo;

/**
 * Created by gaohailong on 2016/5/17.
 */
public class TabHomeHolder extends BaseViewHolder<PageInfo> {

    private TextView tv_title;
    private TextView tv_title2;
    private TextView tv_title3;
    private TextView tv_title4;

    public TabHomeHolder(ViewGroup parent) {
        super(parent, R.layout.item_tabhome);
        tv_title = $(R.id.tv_title);
        tv_title2 = $(R.id.tv_title2);
        tv_title3 = $(R.id.tv_title3);
        tv_title4 = $(R.id.tv_title4);
    }

    @Override
    public void setData(PageInfo data) {
        super.setData(data);
        //
        tv_title.setText(data.getTitle());
        tv_title2.setText(data.getYear()+"  "+data.getScore()+"分  "+data.getType()+"  "+data.getAddr());
        tv_title3.setText("主演:"+data.getActor());
        tv_title4.setText("发布时间:"+data.getUpdatetime());

    }

}
