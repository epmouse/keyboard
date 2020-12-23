package com.zyxj.customerkeyboardlib;

import android.view.View;
import android.widget.EditText;

public interface IKeyboardDialog {

    void show(KeyboardType keyboardType);

    void dismiss();

    boolean isShowing();

    /**
     * 定制键盘顶部布局
     * @param view  传入的view会显示到软键盘顶部
     */
    void setTopCustomerView(View view);

    void setCurrentEditText(EditText editText);

    /**
     * @return  返回当前键盘布局中的所有view的id，用于处理点击键盘外部消失的问题， dialog形式的键盘此方法返回空数组
     */
    int[] getKeyboardViewIds();

    /**
     * 设置键盘按键点击变色效果可用
     * @param clickEventEnable
     */
    void setClickEventEnable(boolean clickEventEnable);

    void setWhetherRandom(boolean isRandom);
}
