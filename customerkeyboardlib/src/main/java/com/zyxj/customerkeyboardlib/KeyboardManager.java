package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * 改为build模式创建键盘，比原来的继承方式更灵活。
 * 说明： 目前键盘用两种方式实现，一种是直接把软件盘添加到当前页面，也就是BaseKeyboardLayout的模式，另一种是把软件盘放到popUpWindow中。
 * 两种方式优劣：分别用1 和 2 代表
 * 1. 优势： 可以根据输入框位置 折叠页面（需要页面有scrollview支持）
 * 劣势：由于实现原理是把键盘布局addView加入了页面的根布局中 ，所以键盘的弹出位置需要用户自己控制，不会默认在底部。  并且无弹出动画
 * 2. 优势：因为是popWindow形式，所以能保证总是在页面顶层，并且是底部弹出,用户不需要考虑适配。自带动画
 * 劣势：无法自动折叠页面布局。
 */
public class KeyboardManager implements IKeyboardManager {
    private IKeyboardDialog keyboardDialog;
    private View topView;
    private OnKeyboardDismissListener onKeyboardDismissListener;
    private OnKeyboardShowListener onKeyboardShowListener;
    private Activity activity;
    private KeyboardOutsideTouchEventUtils keyboardOutsideTouchEventUtils;
    private KeyboardType keyboardType;
    private boolean haveClickColor;
    private boolean isRandom;

    private KeyboardManager(Builder builder) {
        this.activity = builder.activity;
        keyboardOutsideTouchEventUtils = new KeyboardOutsideTouchEventUtils(activity, this);
        keyboardOutsideTouchEventUtils.setIgnoreViewIds(builder.ignoreIds);
        keyboardOutsideTouchEventUtils.setOutsideTouchable(builder.touchable);
        keyboardOutsideTouchEventUtils.setRootView(builder.rootView);
        this.keyboardDialog = builder.keyboardDialog;
        this.topView = builder.topView;
        this.keyboardType = builder.keyboardType;
        this.onKeyboardDismissListener = builder.onKeyboardDismissListener;
        this.onKeyboardShowListener = builder.onKeyboardShowListener;
        this.haveClickColor = builder.haveClickColor;
        this.isRandom = builder.isRandom;
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
        if (keyboardDialog == null) {
            throw new RuntimeException(this.getClass().getName() + ".onCreateKeyboardDialog()不能返回null，请在onCreateKeyboardDialog()方法中创建键盘依附的View（IKeyboardDialog）");
        }
        keyboardDialog.setTopCustomerView(topView);
        keyboardDialog.setClickEventEnable(haveClickColor);
        if (keyboardDialog instanceof KeyboardPopWindow) {
            PopupWindow popupWindow = ((KeyboardPopWindow) keyboardDialog).getPopupWindow();
            if (popupWindow != null) {
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (onKeyboardDismissListener != null) {
                            onKeyboardDismissListener.onHide();
                        }
                    }
                });
            }
        }
        if (keyboardOutsideTouchEventUtils != null) {
            keyboardOutsideTouchEventUtils.setDefaultIgnoreIds(keyboardDialog.getKeyboardViewIds());
            keyboardOutsideTouchEventUtils.action();
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                if (keyboardDialog != null) {
                    keyboardDialog.setCurrentEditText(view);
                    keyboardDialog.setWhetherRandom(isRandom);
                    keyboardDialog.show(keyboardType == null ? KeyboardType.BIG_CONFIRM_BUTTON : keyboardType);
                    if (onKeyboardShowListener != null) {
                        onKeyboardShowListener.onShow();
                    }
                }
            }
        });
    }

    @Override
    public KeyboardManager setCurrentEditText(EditText editText) {
        keyboardDialog.setCurrentEditText(editText);
        Utils.hideSystemSofeKeyboard(activity, editText);
        return this;
    }

    @Override
    public boolean isShowing() {
        return keyboardDialog != null && keyboardDialog.isShowing();
    }

    @Override
    public IKeyboardManager setKeyboardType(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
        return this;
    }

    public static class Builder {
        private IKeyboardDialog keyboardDialog;
        private OnKeyboardDismissListener onKeyboardDismissListener;
        private OnKeyboardShowListener onKeyboardShowListener;
        private Activity activity;
        private KeyboardType keyboardType;
        private boolean touchable = true;//默认可点击消失
        private int[] ignoreIds;
        private boolean haveClickColor = true;
        private View topView;
        private View rootView;
        private boolean isRandom;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 创建所用的键盘模式
         *
         * @param keyboardDialog 目前有 KeyboardPopWindow 和 BaseKeyboardLayout 两种
         * @return
         */
        public Builder setKeyboardDialog(IKeyboardDialog keyboardDialog) {
            this.keyboardDialog = keyboardDialog;
            return this;
        }

        /**
         * 用于处理点击dialog之外区域消失，不传则默认获取当前activity的跟布局，一般用于Activity和Fragment之外的布局，比如在dialog上弹出自定义键盘时
         */
        public Builder setRootView(View rootView){
            this.rootView = rootView;
            return this;
        }

        /**
         * 创建软件盘顶部布局
         */
        public Builder setTopView(View topView) {
            this.topView = topView;
            return this;
        }

        public Builder setOnKeyboardDismissListener(OnKeyboardDismissListener onKeyboardDismissListener) {
            this.onKeyboardDismissListener = onKeyboardDismissListener;
            return this;
        }

        public Builder setOnKeyboardShowListener(OnKeyboardShowListener onKeyboardShowListener) {
            this.onKeyboardShowListener = onKeyboardShowListener;
            return this;
        }

        /**
         * 处理软键盘弹窗外部点击消失，直接使用dialog的setOutsideTouchEnable处理，无法排除特殊view，比如返回按钮  清除金额按钮。
         *
         * @param ids 点击不会导致键盘关闭的view  id
         */
        public Builder setIgnoreViewIds(int... ids) {
            this.ignoreIds = ids;
            return this;
        }

        /**
         * @param touchable default true
         */
        public Builder setOutsideTouchable(boolean touchable) {
            this.touchable = touchable;
            return this;
        }

        /**
         * 设置是否是随机键盘
         *
         * @param isRandom default false
         */
        public Builder setWhetherRandom(boolean isRandom) {
            this.isRandom = isRandom;
            return this;
        }

        /**
         * 键盘样式类型
         */
        public Builder setKeyboardType(KeyboardType keyboardType) {
            this.keyboardType = keyboardType;
            return this;
        }

        /**
         * @return 重写此方法返回 false 屏蔽键盘按键点击变色效果  默认为true
         */
        public Builder setClickEventStatus(boolean haveColor) {
            this.haveClickColor = haveColor;
            return this;
        }

        public KeyboardManager build() {
            if (keyboardDialog == null) {
                keyboardDialog = new KeyboardPopWindow(activity);
            }
            if (keyboardType == null) {
                keyboardType = KeyboardType.BIG_CONFIRM_BUTTON;
            }
            return new KeyboardManager(this);
        }

    }

}


