package cn.xm.xmvideoplayer.data.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xm.xmvideoplayer.entity.PageDetailInfo;
import cn.xm.xmvideoplayer.entity.PageInfo;
import cn.xm.xmvideoplayer.utils.ACache;
import cn.xm.xmvideoplayer.utils.LogUitl;
import cn.xm.xmvideoplayer.utils.NetWorkUtil;


/**
 * Created by WANG on 2016/7/27.
 */
public class JsoupApi {

    /**
     * url编码
     */
    public static final String urlencodde = "gb2312";
    /**
     * useragent
     */
    public static final String useragent = "Mozilla";

    /**
     * 链接超时时间
     */
    public static final int timeout = 5000;

    /**
     * 缓存时间13分
     */
    public static final int cache_time = 13 * ACache.TIME_MINUTE;
    /**
     * 主链接
     */
    private static String BaseUrl = "http://www.dytt.com";

    /**
     * 搜索链接
     */
    private static String BaseUrlSearch = "http://www.dytt.com/search.asp?page=";


    /**
     * 最新更新链接
     */
    //private static final String BaseUrlUpdate = "http://www.dytt.com/top/lastupdate";
    private static final String BaseUrlUpdate = "http://www.dytt.com/top/toplist";
    public static final String typeUpdate = "typeUpdate";

    /**
     * 最新电影链接http://www.dytt.com/top/toplist_15.html
     */
    private static String BaseUrlFilm = "http://www.dytt.com/top/lastupdate_15";
    //    private static String BaseUrlFilm = "http://www.dytt.com/top/toplist_15";
    public static final String typeFilm = "typeFilm";

    /**
     * 最新电视剧链接http://www.dytt.com/top/toplist_16.html
     */
    private static String BaseUrlTv = "http://www.dytt.com/top/lastupdate_16";
    //    private static String BaseUrlTv = "http://www.dytt.com/top/toplist_16";
    public static final String typeTv = "typeTv";

    /**
     * 最新动漫链接http://www.dytt.com/top/toplist_7.html
     */
    private static String BaseUrlCartoon = "http://www.dytt.com/top/lastupdate_7";
    //    private static String BaseUrlCartoon = "http://www.dytt.com/top/toplist_7";
    public static final String typeCartoon = "typeCartoon";

    /**
     * 最新综艺链接http://www.dytt.com/top/toplist_8.html
     */
    private static String BaseUrlVariety = "http://www.dytt.com/top/lastupdate_8";
    //    private static String BaseUrlVariety = "http://www.dytt.com/top/toplist_8";
    public static final String typeVariety = "typeVariety";

    /**
     * wifi状态
     */
    private static final int StatueWifi = 111;

    /**
     * wifi状态
     */
    private static final int StatueAll = 222;

    private File mFile;

    /**
     * 获得对象
     *
     * @return jsoup对象
     */
    public static JsoupApi NewInstans(File context) {
        return new JsoupApi(context);
    }

    /**
     * 构造
     *
     * @param context
     */
    private JsoupApi(File context) {
        this.mFile = context;
    }

    /**
     * 获取doc对象
     *
     * @param URL    链接
     * @param method 访问的方法MethodGet MethodPost
     * @return doc对象
     */
    private Document GetDoc(String URL, Connection.Method method) {
        Document mdoc;
        ACache aCache = ACache.get(mFile);
        //判断是否有缓存
        try {
            String doc;
            if ((doc = aCache.getAsString(URL)) != null) {//如果有数据，则返回
                mdoc = Jsoup.parse(doc);
                return mdoc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //异常或者没有数据，则正常访问并存储
        //需要wifi，wifi未连接，则拦截
        if (statueNeed() == StatueWifi && !NetWorkUtil.isWifiConnected()) {
            return null;
        }
        try {
            mdoc = Jsoup.connect(URL)
                    .timeout(timeout)
                    .method(method)
                    .userAgent(useragent)
                    .execute()
                    .parse();
        } catch (IOException e) {
            e.printStackTrace();
            LogUitl.LogIMsg("jsoupapi_GetDoc_line:117" + "  出现异常");
            return null;
        }
        //保存缓存，设置过期时间
        aCache.put(URL, mdoc.html(), cache_time);
        return mdoc;

    }

    /**
     * 设置需要的链接wifi，移动连接
     *
     * @return 所需要的状态
     */
    private int statueNeed() {
        return StatueAll;
    }

    /**
     * 判断需要的网络状态，是否链接失败
     *
     * @param doc
     * @return 如果需要的状态不可用，则返回null
     */
    private boolean judgeConState(Document doc) {
        if (doc == null) {//判断网络链接
            switch (statueNeed()) {
                case StatueWifi: {//wifi状态
                    if (!NetWorkUtil.isWifiConnected()) {
                        return true;
                    }
                }
                case StatueAll: {//所有网络
                    if (!NetWorkUtil.isNetworkConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取详情数据
     *
     * @param URL 页面链接
     * @return doc为null则网络链接有问题
     */
    public PageDetailInfo GetPageDetail(String URL) {
        Document doc = null;
        do {
            doc = GetDoc(URL, Connection.Method.GET);
            if (judgeConState(doc)) return null;
        } while (doc == null);

        String title = null;
        String cover = null;
        String smalltext = null;
        String alltext = null;
        String actor = null;
        List<List> listsdownload = null;
        try {
            //标题
            title = doc.select("div.movie h1").first().html();
            //封面链接
            cover = doc.select("div.pic").first().getElementsByTag("img").first().select("img").attr("src").trim();
            //剧情介绍
            smalltext = doc.select("div.smalltext").first().html();
            alltext = doc.select("div.alltext").first().html();
            //主演,更新时间
            actor = doc.select("div.movie ul").first().outerHtml().replaceAll("li", "br");
            //下载地址
            Elements downlist = doc.select("div.downlist script");
            //
            listsdownload = new ArrayList<List>();
            for (Element et : downlist) {
                List<String> lists = new ArrayList<>();
                if (et.html().contains("GvodUrls")) {
                    Pattern pat = Pattern.compile("\"(.*?)\"");
                    Matcher mat = pat.matcher(et.html());
                    if (mat.find()) {
                        //下载地址
                        String urlDeCode = null;
                        try {
                            urlDeCode = URLDecoder.decode(mat.group(1), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //
                        String[] split = urlDeCode.split("###");
                        for (int i = 0; i < split.length; i++) {
                            if (!"".equals(split[i])) {
                                lists.add(split[i]);
                            }
                        }
                        Collections.reverse(lists);
                        listsdownload.add(lists);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new PageDetailInfo(URL, title, cover, smalltext, alltext, actor, listsdownload);
    }

    /**
     * 获取信息封面链接
     *
     * @param URL 页面链接
     * @return 返回null则网络链接有问题
     */
    public String GetPageDetailCover(String URL) {
        Document doc = null;
        do {
            doc = GetDoc(URL, Connection.Method.GET);
            if (judgeConState(doc)) return null;
        }
        while (doc == null);
        //封面链接
        String cover = doc.select("div.pic").first().getElementsByTag("img").first().select("img").attr("src").trim();
        return cover;
    }

    /**
     * 获取搜索结果数据
     * http://www.dytt.com/search.asp?page=4&searchword=%C0%CF&searchtype=-1
     *
     * @param kw 关键字
     * @return 返回null则网络链接有问题
     */
    public List<PageInfo> GetPageSearch(int page, String kw) {
        Document doc = null;
        do {
            doc = GetDoc(DealUrlSearch(page, kw), Connection.Method.POST);
            if (judgeConState(doc)) return null;
        }
        while (doc == null);
        List<PageInfo> pageInfos = GetPageDeal(doc, true);
        return pageInfos;
    }

    /**
     * 获取展示数据
     *
     * @param Page 页码
     * @param type 类型
     * @return 返回null则网络链接有问题
     */
    public List<PageInfo> GetPage(int Page, String type) {
        Document doc = null;
        do {
            doc = GetDoc(DealUrl(Page, type), Connection.Method.GET);
            if (judgeConState(doc)) return null;
        }
        while (doc == null);
        List<PageInfo> pageInfos = GetPageDeal(doc, false);
        return pageInfos;
    }

    /**
     * 处理展示,搜索数据
     *
     * @param doc doc对象
     * @return 解析失败list长度0
     */
    private List<PageInfo> GetPageDeal(Document doc, boolean needPage) {
        ArrayList<PageInfo> pageInfos = new ArrayList<>();
        String page = "";
        try {
            Elements movielist = doc.select("div.movielist li");
            for (Element et : movielist) {
                String score = et.select("p.s4").first().html();
                String type = et.select("p.s5").first().html();
                String actor = et.select("p.s6").first().html();
                String updatetime = et.select("p.s7").first().html();
                String ahref = BaseUrl + et.select("p.s1").first().select("a").attr("href");
                String title = et.select("p.s1").first().select("a").html();
                String year = et.select("p").first().nextElementSibling().html();
                String addr = et.select("p").first().nextElementSibling().nextElementSibling().html().replace("&nbsp;", "");
                pageInfos.add(new PageInfo(score, type, actor, updatetime, ahref, title, year, addr, page));
                //Log.i("msg", "  页码：" + page + "  评分：" + score + "  网址:" + ahref + "  标题:" + title + "  类型:" + type + "  主演:" + actor + "  更新时间:" + updatetime + "  年代:" + year + "  地区:" + addr);
            }
            //防止npe
            if (needPage && pageInfos.size() > 0) {
                page = doc.select("span.fbbnt input").first().nextElementSibling().attr("onclick").substring(13, 14);
                pageInfos.get(0).setPage(page);
                //LogUitl.LogIMsg("Page:"+page);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pageInfos;
    }

    /**
     * 处理链接格式,展示页面
     *
     * @param Page 页面
     * @return 处理后的结果
     */
    private String DealUrl(int Page, String type) {
        //处理页码
        String html = ".html";
        String mPage = Page + "";
        if (Page <= 1) {
            mPage = "";
        }
        //处理链接
        switch (type) {
            case typeUpdate:
                return BaseUrlUpdate + mPage + html;
            case typeFilm:
                return BaseUrlFilm + mPage + html;
            case typeTv:
                return BaseUrlTv + mPage + html;
            case typeCartoon:
                return BaseUrlCartoon + mPage + html;
            case typeVariety:
                return BaseUrlVariety + mPage + html;
        }
        return BaseUrl;
    }

    /**
     * 处理链接格式，搜索页面
     * http://www.dytt.com/search.asp?page=4&searchword=%C0%CF&searchtype=-1
     *
     * @param kw 关键词（不需要编码转换）
     * @return 处理后的结果
     */
    private String DealUrlSearch(int page, String kw) {
        String kwgb2312 = "";
        String field1 = "&searchword=";
        String field2 = "&searchtype=-1";
        try {
            kwgb2312 = URLEncoder.encode(kw, urlencodde);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return BaseUrlSearch + page + field1 + "" + kwgb2312 + field2;
    }
}
