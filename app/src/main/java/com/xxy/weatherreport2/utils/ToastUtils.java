package com.xxy.weatherreport2.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showLongToast(Context context, CharSequence xxy) {
        // 第一个参数：当前的上下文环境。可用getApplicationContext()或this
        // 第二个参数：要显示的字符串。也可是R.string中字符串ID
        // 第三个参数：显示的时间长短。Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)，也可以使用毫秒如2000ms
        Toast.makeText(context.getApplicationContext(), xxy, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, CharSequence xxy) {
        Toast.makeText(context.getApplicationContext(), xxy, Toast.LENGTH_SHORT).show();
    }


}
