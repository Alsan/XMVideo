package cn.xm.xmvideoplayer.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by WANG on 2016/7/27.
 * 页面信息
 */
public class PageInfo extends RealmObject implements Parcelable {
    private String score;
    private String type;
    private String actor;
    private String updatetime;
    private String ahref;
    private String title;
    private String year;
    private String addr;
    private String page;

    @Override
    public String toString() {
        return "PageInfo{" +
                "score='" + score + '\'' +
                ", type='" + type + '\'' +
                ", actor='" + actor + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", ahref='" + ahref + '\'' +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", addr='" + addr + '\'' +
                ", page='" + page + '\'' +
                '}';
    }

    public PageInfo() {
    }

    public PageInfo(String score, String type, String actor, String updatetime, String ahref, String title, String year, String addr, String page) {
        this.score = score;
        this.type = type;
        this.actor = actor;
        this.updatetime = updatetime;
        this.ahref = ahref;
        this.title = title;
        this.year = year;
        this.addr = addr;
        this.page = page;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getAhref() {
        return ahref;
    }

    public void setAhref(String ahref) {
        this.ahref = ahref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.score);
        dest.writeString(this.type);
        dest.writeString(this.actor);
        dest.writeString(this.updatetime);
        dest.writeString(this.ahref);
        dest.writeString(this.title);
        dest.writeString(this.year);
        dest.writeString(this.addr);
        dest.writeString(this.page);
    }

    protected PageInfo(Parcel in) {
        this.score = in.readString();
        this.type = in.readString();
        this.actor = in.readString();
        this.updatetime = in.readString();
        this.ahref = in.readString();
        this.title = in.readString();
        this.year = in.readString();
        this.addr = in.readString();
        this.page = in.readString();
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel source) {
            return new PageInfo(source);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}
