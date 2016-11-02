package cn.xm.xmvideoplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by WANG on 2016/7/27.
 * 页面详情
 */
public class PageDetailInfo extends RealmObject implements Parcelable {

    /**
     * 时间
     */
    private long datetime;

    /**
     * 页面链接
     */
    private String Url;
    /**
     * 标题
     */
    private String title;
    /**
     * 封面链接
     */
    private String cover;
    /**
     * 剧情介绍
     */
    private String smalltext;
    private String alltext;

    /**
     * 主演,更新时间
     */
    private String actor;
    /**
     * 下载地址
     */
    @Ignore
    List<List> downlist;

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    /**
     * 搜索信息
     */
    private PageInfo pageInfo;

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSmalltext() {
        return smalltext;
    }

    public void setSmalltext(String smalltext) {
        this.smalltext = smalltext;
    }

    public String getAlltext() {
        return alltext;
    }

    public void setAlltext(String alltext) {
        this.alltext = alltext;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public List<List> getDownlist() {
        return downlist;
    }

    public void setDownlist(List<List> downlist) {
        this.downlist = downlist;
    }

    @Override
    public String toString() {
        return "PageDetailInfo{" +
                "Url='" + Url + '\'' +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", smalltext='" + smalltext + '\'' +
                ", alltext='" + alltext + '\'' +
                ", actor='" + actor + '\'' +
                ", pageInfo=" + pageInfo.toString() +
                '}';
    }

    /**
     * 页面详情构造
     *
     * @param url
     * @param title
     * @param cover
     * @param smalltext
     * @param alltext
     * @param actor
     * @param downlist
     * @param pageInfo  pageinfo对象
     */
    public PageDetailInfo(String url, String title, String cover, String smalltext, String alltext, String actor, List<List> downlist, PageInfo pageInfo) {
        this.Url = url;
        this.title = title;
        this.cover = cover;
        this.smalltext = smalltext;
        this.alltext = alltext;
        this.actor = actor;
        this.downlist = downlist;
        this.pageInfo = pageInfo;
    }

    /**
     * 页面详情构造
     *
     * @param url
     * @param title
     * @param cover
     * @param smalltext
     * @param alltext
     * @param actor
     * @param downlist
     */
    public PageDetailInfo(String url, String title, String cover, String smalltext, String alltext, String actor, List<List> downlist) {
        this.Url = url;
        this.title = title;
        this.cover = cover;
        this.smalltext = smalltext;
        this.alltext = alltext;
        this.actor = actor;
        this.downlist = downlist;
    }

    public PageDetailInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.datetime);
        dest.writeString(this.Url);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.smalltext);
        dest.writeString(this.alltext);
        dest.writeString(this.actor);
        dest.writeList(this.downlist);
        dest.writeParcelable(this.pageInfo, flags);
    }

    protected PageDetailInfo(Parcel in) {
        this.datetime = in.readLong();
        this.Url = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.smalltext = in.readString();
        this.alltext = in.readString();
        this.actor = in.readString();
        this.downlist = new ArrayList<List>();
        in.readList(this.downlist, List.class.getClassLoader());
        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
    }

    public static final Creator<PageDetailInfo> CREATOR = new Creator<PageDetailInfo>() {
        @Override
        public PageDetailInfo createFromParcel(Parcel source) {
            return new PageDetailInfo(source);
        }

        @Override
        public PageDetailInfo[] newArray(int size) {
            return new PageDetailInfo[size];
        }
    };
}
