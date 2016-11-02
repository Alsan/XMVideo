package cn.xm.xmvideoplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.callback.UpdateCheckCB;
import org.lzh.framework.updatepluginlib.callback.UpdateDownloadCB;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.strategy.UpdateStrategy;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.xm.xmvideoplayer.R;
import cn.xm.xmvideoplayer.constant.AdConstant;
import cn.xm.xmvideoplayer.core.BaseActivity;
import cn.xm.xmvideoplayer.ui.activity.act_home;


public class act_Splash extends BaseActivity implements SplashADListener {

    @SuppressWarnings("unused")
    private SplashAD splashAD;
    private ViewGroup container;
    private Handler handler = new Handler();
    public boolean canJump = false;
    @Bind(R.id.app_logo)
    ImageView app_log;

    @Override
    public String setUmengTag() {
        return "act_splash";
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_launch;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        container = ButterKnife.findById(act_Splash.this, R.id.splash_container);
        Glide.with(this)
                .load(R.drawable.background)
                .fitCenter()
                .into(app_log);
    }


    /**
     * 检查版本更新
     */
    private void checkUpdate() {
        //检查更新
        UpdateBuilder.create()
                .checkCB(new UpdateCheckCB() {
                    @Override
                    public void onCheckError(int code, String errorMsg) {
                        //Toast.makeText(act_Splash.this, "更新失败：code:" + code, Toast.LENGTH_SHORT).show();
                        starAd();
                    }

                    @Override
                    public void onUserCancel() {
                        Toast.makeText(act_Splash.this, "取消更新", Toast.LENGTH_SHORT).show();
                        starAd();
                    }

                    @Override
                    public void onCheckIgnore(Update update) {}

                    @Override
                    public void hasUpdate(Update update) {}

                    @Override
                    public void noUpdate() {
                        //Toast.makeText(act_Splash.this, "最新版本", Toast.LENGTH_SHORT).show();
                        starAd();
                    }
                })
                // apk下载的回调
                .downloadCB(new UpdateDownloadCB() {
                    @Override
                    public void onUpdateStart() {

                    }

                    @Override
                    public void onUpdateComplete(File file) {

                    }

                    @Override
                    public void onUpdateProgress(long current, long total) {

                    }

                    @Override
                    public void onUpdateError(int code, String errorMsg) {
                        //Toast.makeText(act_Splash.this, "下载失败：code:" + code, Toast.LENGTH_SHORT).show();
                        starAd();
                    }
                })
                .strategy(new UpdateStrategy() {
                    @Override
                    public boolean isShowUpdateDialog(Update update) {
                        // 有新更新直接展示
                        return true;
                    }

                    @Override
                    public boolean isAutoInstall() {
                        return true;
                    }

                    @Override
                    public boolean isShowDownloadDialog() {
                        // 展示下载进度
                        return true;
                    }
                })
                .check(act_Splash.this);
    }

    /**
     * 开始广告
     */
    public void starAd() {
        splashAD = new SplashAD(this, container, AdConstant.APPID, AdConstant.SplashPosID, this);
    }

    @Override
    public void initToolBar() {

    }

    /**
     * 开屏广告现已增加新的接口，可以由开发者在代码中设置开屏的超时时长
     * SplashAD(Activity activity, ViewGroup container, String appId, String posId, SplashADListener adListener, int fetchDelay)
     * fetchDelay参数表示开屏的超时时间，单位为ms，取值范围[3000, 5000]。设置为0时表示使用广点通的默认开屏超时配置
     * <p/>
     * splashAD = new SplashAD(this, container, AdConstant.APPID, AdConstant.SplashPosID, this, 3000);可以设置超时时长为3000ms
     */
    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int errorCode) {
        Log.i("AD_DEMO", "LoadSplashADFail, eCode=" + errorCode);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /** 如果加载广告失败，则直接跳转 */
                act_Splash.this.startActivity(new Intent(act_Splash.this, act_home.class));
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                act_Splash.this.finish();
            }
        }, 1500);

    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            this.startActivity(new Intent(this, act_home.class));
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUpdate();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /**
     * 开屏页最好禁止用户对返回按钮的控制
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
