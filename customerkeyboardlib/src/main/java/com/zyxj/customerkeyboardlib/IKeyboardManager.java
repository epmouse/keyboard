package com.zyxj.customerkeyboardlib;

import android.widget.EditText;

public interface IKeyboardManager {
    /**
     * popUpWindow形式的弹窗，需要在退出页面的时候先dismiss，否则会报 windowManager$BadTokenException
     */
    void hideSoftInput();

    //打开软键盘
    void showInputMethod(EditText view);

    /**
     * 键盘样式类型,暴露此方法用于一个页面内切换不通过的键盘样式
     */
    IKeyboardManager setKeyboardType(KeyboardType keyboardType);

    /**
     * 用于键盘显示状态下切换edittext
     */
    IKeyboardManager setCurrentEditText(EditText editText);

    boolean isShowing();
}
