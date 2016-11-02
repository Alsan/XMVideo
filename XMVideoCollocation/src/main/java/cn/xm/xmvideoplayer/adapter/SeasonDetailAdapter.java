package cn.xm.xmvideoplayer.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by gaohailong on 2016/5/17.
 */
public class SeasonDetailAdapter extends RecyclerArrayAdapter<String> {

    public SeasonDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new SeasonDetailHolder(parent,getAllData());
    }

}
