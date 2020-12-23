package com.zyxj.customerkeyboardlib;

import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

public class Utils {
    //打印view所对应的字符串id
    public static void idLog(Context context, String tag, View view) {
        try {
            String resourceName = context.getResources().getResourceName(view.getId());
            Log.d(tag, resourceName);
        } catch (Exception e) {

        }
    }

    //合并两个int数组
    public static int[] concatArray(int[] a, int[] b) {
        if (a == null || b == null) {
            throw new RuntimeException("请保证合并的两个数组都不为null");
        }
        if (b.length == 0)
            return a;
        if (a.length == 0) {
            return b;
        }
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, c.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public static void hideSystemSofeKeyboard(Context context, EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
