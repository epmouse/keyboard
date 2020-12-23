package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class KeyboardPopWindow extends BaseKeyboardLayout implements IKeyboardDialog {
    private PopupWindow popupWindow;

    public KeyboardPopWindow(Activity activity) {
        super(activity, null);
    }

    /**
     * @param activity
     * @param rootView popUpWindow.showAtLocation(View parent, int gravity, int x, int y) 中的parent
     */
    public KeyboardPopWindow(Activity activity, ViewGroup rootView) {
        super(activity, rootView);
    }

    @Override
    public void setContentView(View contentView) {
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.BorrowKeyboardAnimation);
    }

    @Override
    public int[] getKeyboardViewIds() {
        return new int[0];
    }


    /**
     * show方法必需在activity加载完成后调用，建议在view.post方法中调用。
     */
    @Override
    public void show(KeyboardType keyboardType) {
        super.show(keyboardType);
        popupWindow.showAtLocation(getPageRootView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void dismiss() {
        if (popupWindow != null)
            popupWindow.dismiss();
    }

    @Override
    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

}
