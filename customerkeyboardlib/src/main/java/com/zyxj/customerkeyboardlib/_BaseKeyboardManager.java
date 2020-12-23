package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 *  说明： 目前键盘用两种方式实现，一种是直接把软件盘添加到当前页面，也就是BaseKeyboardLayout的模式，另一种是把软件盘放到popUpWindow中。
 *  两种方式优劣：分别用1 和 2 代表
 *  1. 优势： 可以根据输入框位置 折叠页面（需要页面有scrollview支持）
 *     劣势：由于实现原理是把键盘布局addView加入了页面的根布局中 ，所以键盘的弹出位置需要用户自己控制，不会默认在底部。  并且无弹出动画
 *  2. 优势：因为是popWindow形式，所以能保证总是在页面顶层，并且是底部弹出,用户不需要考虑适配。自带动画
 *     劣势：无法自动折叠页面布局。
 *
 */
public abstract class _BaseKeyboardManager implements IKeyboardManager {
    protected IKeyboardDialog keyboardDialog;
    protected OnKeyboardDismissListener onKeyboardDismissListener;
    protected OnKeyboardShowListener onKeyboardShowListener;
    protected Activity activity;
    protected KeyboardOutsideTouchEventUtils keyboardOutsideTouchEventUtils;
    protected KeyboardType keyboardType;

    protected _BaseKeyboardManager(Activity activity) {
        this.activity = activity;
        keyboardOutsideTouchEventUtils = new KeyboardOutsideTouchEventUtils(activity, this);
    }

    @Override
    public void hideSoftInput() {
        if (keyboardDialog != null && keyboardDialog.isShowing()) {
            keyboardDialog.dismiss();
            VibrateUtils.stop();
        }
    }

    @Override
    public void showInputMethod(final EditText view) {
        Utils.hideSystemSofeKeyboard(activity, view);
        if (keyboardDialog != null && keyboardDialog.isShowing()) {
            return;
        }
        keyboardDialog = onCreateKeyboardDialog();
        if (keyboardDialog == null) {
            throw new RuntimeException(this.getClass().getName() + ".onCreateKeyboardDialog()不能返回null，请在onCreateKeyboardDialog()方法中创建键盘依附的View（IKeyboardDialog）");
        }
        keyboardDialog.setTopCustomerView(onCreateTopView());
        keyboardDialog.setClickEventEnable(getClickEventStatus());
        if (keyboardDialog instanceof KeyboardPopWindow)
            ((KeyboardPopWindow) keyboardDialog).getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (onKeyboardDismissListener != null) {
                        onKeyboardDismissListener.onHide();
                    }
                }
            });
        if (keyboardOutsideTouchEventUtils != null) {
            keyboardOutsideTouchEventUtils.setDefaultIgnoreIds(keyboardDialog.getKeyboardViewIds());
            keyboardOutsideTouchEventUtils.action();
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if (keyboardDialog != null) {
                    keyboardDialog.setCurrentEditText(view);
                    keyboardDialog.show(keyboardType == null ? KeyboardType.BIG_CONFIRM_BUTTON : keyboardType);
                    if (onKeyboardShowListener != null) {
                        onKeyboardShowListener.onShow();
                    }
                }
            }
        });
    }

    /**
     * @return 重写此方法返回 false 屏蔽键盘按键点击变色效果  默认为true
     */
    protected boolean getClickEventStatus() {
        return true;
    }

    public _BaseKeyboardManager setOnKeyboardDismissListener(OnKeyboardDismissListener onKeyboardDismissListener) {
        this.onKeyboardDismissListener = onKeyboardDismissListener;
        return this;
    }

    public IKeyboardManager setOnKeyboardShowListener(OnKeyboardShowListener onKeyboardShowListener) {
        this.onKeyboardShowListener = onKeyboardShowListener;
        return this;
    }

    public _BaseKeyboardManager setIgnoreViewIds(int... ids) {
        if (ids != null && ids.length > 0) {
            keyboardOutsideTouchEventUtils.setIgnoreViewIds(ids);
        }
        return this;
    }

    public _BaseKeyboardManager setOutsideTouchable(boolean touchable) {
        keyboardOutsideTouchEventUtils.setOutsideTouchable(touchable);
        return this;
    }

    @Override
    public _BaseKeyboardManager setKeyboardType(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
        return this;
    }

    @Override
    public _BaseKeyboardManager setCurrentEditText(EditText editText) {
        keyboardDialog.setCurrentEditText(editText);
        Utils.hideSystemSofeKeyboard(activity, editText);
        return this;
    }

    @Override
    public boolean isShowing() {
        return keyboardDialog != null && keyboardDialog.isShowing();
    }

    /**
     * 重写创建dialog,返回null 则默认创建 KeyboardPopWindow
     */
    protected abstract IKeyboardDialog onCreateKeyboardDialog();

    /**
     * 重写创建键盘顶部的view
     */
    protected abstract View onCreateTopView();

}


