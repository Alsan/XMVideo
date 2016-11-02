package cn.xm.xmvideoplayer.data.realm;

import android.content.Context;
import android.provider.Settings;
import android.text.format.DateUtils;

import cn.xm.xmvideoplayer.entity.PageDetailInfo;
import cn.xm.xmvideoplayer.entity.PageInfo;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by WANG on 2016/7/28.
 */
public class DbFav {

    /**
     * reaml对象
     */
    private static Realm myRealm;

    /**
     * 创建对象
     *
     * @param context
     * @param name
     */
    public static DbFav Builder(Context context, String name) {
        myRealm = Realm.getInstance(
                new RealmConfiguration.Builder(context)
                        .deleteRealmIfMigrationNeeded()
                        .name(name + ".realm")
                        .build()
        );
        return new DbFav();
    }

    /**
     * 插入数据
     *
     * @param pageDetailInfo
     */
    public Boolean Insert(PageDetailInfo pageDetailInfo, PageInfo pageInfo) {
        //插入时间
        pageDetailInfo.setDatetime(System.currentTimeMillis());
        //设置pageinfo数据
        pageDetailInfo.setPageInfo(pageInfo);
        //存入数据库
        myRealm.beginTransaction();
        myRealm.copyToRealm(pageDetailInfo);
        myRealm.commitTransaction();
        if (FindItemIsExit(pageDetailInfo.getUrl())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找所有数据
     *
     * @return
     */
    public RealmResults<PageDetailInfo> FindAll() {
        return myRealm
                .where(PageDetailInfo.class)
                .findAll()
                .sort("datetime", Sort.DESCENDING);
    }

    /**
     * 查询所有是否存在
     *
     * @return
     */
    public Boolean FindAllIsExit() {
        RealmResults<PageDetailInfo> result = FindAll();
        if (result.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询是否存在
     *
     * @param url uri链接
     * @return
     */
    public Boolean FindItemIsExit(String url) {
        RealmResults<PageDetailInfo> result = FindItem(url);
        if (result.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询单条结果
     *
     * @param url
     * @return
     */
    public RealmResults<PageDetailInfo> FindItem(String url) {
        return myRealm.where(PageDetailInfo.class)
                .equalTo("Url", url)
                .findAll();
    }

    /**
     * 删除单条结果
     *
     * @param url
     * @return
     */
    public Boolean DeleteItem(String url) {
        RealmResults<PageDetailInfo> result = FindItem(url);
        if (result.size() <= 0) {
            return true;
        }
        myRealm.beginTransaction();
        boolean isResult = result.deleteAllFromRealm();
        myRealm.commitTransaction();
        return isResult;
    }

    /**
     * 关闭数据库
     */
    public void Close() {
        if (myRealm != null && !myRealm.isClosed()) {
            myRealm.close();
        }
    }
}
