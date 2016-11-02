package cn.xm.xmvideoplayer.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by WANG on 2016/7/29.
 */
public class SnackbarUtil {

    /**
     * 短时间显示
     *
     * @param view   view对象
     * @param string 显示内容
     */
    public static void SnackBarShort(View view, String string) {
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT).show();
    }
}
