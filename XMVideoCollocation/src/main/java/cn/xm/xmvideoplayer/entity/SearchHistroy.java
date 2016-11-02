package cn.xm.xmvideoplayer.entity;

import io.realm.RealmObject;

/**
 * Created by WANG on 2016/7/29.
 */
public class SearchHistroy extends RealmObject {

    /**
     * 搜索关键字
     */
    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SearchHistroy() {
    }

    public SearchHistroy(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "SearchHistroy{" +
                "field='" + field + '\'' +
                '}';
    }
}
