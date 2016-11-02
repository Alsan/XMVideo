package cn.xm.xmvideoplayer.listener;

/**
 * Created by WANG on 2016/8/9.
 */
public interface OnFavoriteItemDeleteListener {
    /**
     * 删除条目回调
     *
     * @param title  删除的标题
     * @param isNull 删除后是否为null，true为null
     */
    void onItemDelete(String title, boolean isNull);
}
