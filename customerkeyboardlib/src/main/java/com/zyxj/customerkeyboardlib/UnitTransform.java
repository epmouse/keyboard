package com.zyxj.customerkeyboardlib;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 单位转换工具
 */
class UnitTransform {
    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getApplicationContext().getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp, context.getApplicationContext().getResources().getDisplayMetrics());
    }

    /**
     * px、dp、sp、pt、in、mm单位转换
     *
     * @param unit  转换类型
     * @param value 转换值(float)
     * @return 转换单位后的值
     */
    public static float applyDimension(int unit, float value, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        switch (unit) {
            case 0: // 转换为px(像素)值
                return value;
            case 1: // 转换为dp(密度)值
                return value * metrics.density;
            case 2: // 转换为sp(与刻度无关的像素)值
                return value * metrics.scaledDensity;
            case 3: // 转换为pt(磅)值
                return value * metrics.xdpi * (1.0f / 72);
            case 4: // 转换为in(英寸)值
                return value * metrics.xdpi;
            case 5: // 转换为mm(毫米)值
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }
}
