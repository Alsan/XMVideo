package cn.xm.xmvideoplayer.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.xm.xmvideoplayer.R;

/**
 * Created by gaohailong on 2016/5/17.
 */
public class SearchHisHolder extends BaseViewHolder<String> {

    private TextView tv_title;
    private List<String> list=new ArrayList<>();

    public SearchHisHolder(ViewGroup parent, List<String> list) {
        super(parent, R.layout.item_searchhis);
        tv_title = $(R.id.tv_title);
        this.list=list;
    }

    @Override
    public void setData(String data) {
        super.setData(data);
        //
        tv_title.setText(data);
    }

}
