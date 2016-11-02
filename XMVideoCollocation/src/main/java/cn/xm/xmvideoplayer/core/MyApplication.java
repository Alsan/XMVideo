package cn.xm.xmvideoplayer.core;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.model.UpdateParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

import cn.xm.xmvideoplayer.constant.UpdateConstant;

/**
 * Created by 11 on 2016/3/31.
 */
public class MyApplication extends Application {

    public static Context mAppContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        //在自己的Application中添加如下代码
        LeakCanary.install(this);
        //友盟
        MobclickAgent.openActivityDurationTrack(false);
        //MobclickAgent.setDebugMode(true);
        //LogUitl.LogIMsg(getDeviceInfo(this));
        //检查更新配置
        checkUpadte();
    }

    /**
     * 配置检查更新
     */
    private void checkUpadte() {
        // UpdateConfig为全局配置。当在其他页面中。使用UpdateBuilder进行检查更新时。
        // 对于没传的参数，会默认使用UpdateConfig中的全局配置
        UpdateConfig.getConfig()
                // 必填：数据更新接口
                .url(UpdateConstant.updateUrl)
                // 必填：用于从数据更新接口获取的数据response中。解析出Update实例。以便框架内部处理
                .jsonParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) {
                        Update update;
                        try {
                            JSONObject object = new JSONObject(response);
                            // 此处模拟一个Update对象
                            update = new Update(response);
                            // 此apk包的更新时间
                            update.setUpdateTime(object.getLong("updated_at"));
                            // 此apk包的下载地址
                            update.setUpdateUrl(object.getString("installUrl"));
                            // 此apk包的版本号
                            update.setVersionCode(object.getInt("build"));
                            // 此apk包的版本名称
                            update.setVersionName(object.getString("versionShort"));
                            // 此apk包的更新内容
                            update.setUpdateContent(object.getString("changelog"));
                            // 此apk包是否为强制更新
                            update.setForced(false);
                            // 是否忽略此次版本更新
                            update.setIgnore(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }
                        return update;
                    }
                });
    }

    public static Context getContext() {
        return mAppContext;
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 获取deviceinfo
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
