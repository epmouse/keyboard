package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

/**
 * 通过给editText之外的view设置触摸监听，实现点击其他区域软件盘消失
 */
public class KeyboardOutsideTouchEventUtils {
    private static final String TAG = "KeyboardOutsideTouchEve";

    private Activity activity;
    private IKeyboardManager keyboardManager;
    private int[] ignoreIds;
    private boolean touchable = true;//默认支持点击外部消失
    private int[] defaultIgnoreIds;//当键盘使用BaseKeyboardLayout方式时，键盘相当于页面中的一个布局，所以需要排除本身，否则会造成按一次键盘就会消失的情况
    private View rootView;

    public KeyboardOutsideTouchEventUtils(Activity activity, IKeyboardManager keyboardManager) {
        this.activity = activity;
        this.keyboardManager = keyboardManager;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    /**
     * 目前主要用于排除键盘本身的view，用于处理view式键盘点击键盘本身也会消失的问题
     * @param defaultIgnoreIds
     */
    public void setDefaultIgnoreIds(int[] defaultIgnoreIds) {
        this.defaultIgnoreIds = defaultIgnoreIds;
    }

    public void setIgnoreViewIds(int... ids) {
        this.ignoreIds = ids;
    }

    /**
     * @param touchable default true  是否可以点击外部消失
     */
    public void setOutsideTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void action() {
        if (!touchable) return;
        if (ignoreIds == null)
            ignoreIds = new int[0];
        int[] destIgnoreIds = ignoreIds;
        if (defaultIgnoreIds != null && defaultIgnoreIds.length > 0) {
            destIgnoreIds = Utils.concatArray(ignoreIds, defaultIgnoreIds);
        }
        setOnTouchListener(destIgnoreIds);
    }

    /**
     * @param ignoreViewIds 要排除的viewId
     */
    private void setOnTouchListener(int[] ignoreViewIds) {
        if (activity != null) {
            View pageRootView = getPageRootView();
            searchChild(pageRootView, ignoreViewIds);
        }
    }

    private View getPageRootView() {
        return rootView == null ? activity.getWindow().getDecorView().findViewById(android.R.id.content) : rootView;
    }

    private void searchChild(View viewById, int[] ignoreViewIds) {
        if (viewById instanceof ViewGroup) {
            int childCount = ((ViewGroup) viewById).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ((ViewGroup) viewById).getChildAt(i);
                boolean isContain = false;
                if (ignoreViewIds != null && ignoreViewIds.length > 0) {
                    for (int ignoreViewId : ignoreViewIds) {//排除
                        if (ignoreViewId == childAt.getId()) {
                            isContain = true;
                            break;
                        }
                    }
                }
                if (!isContain && !(childAt instanceof EditText)) {
                    childAt.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                Utils.idLog(activity, TAG, v);
                                if (keyboardManager != null && touchable)
                                    keyboardManager.hideSoftInput();
                            }
                            return false;
                        }
                    });
                }
                searchChild(childAt, ignoreViewIds);
            }
        }
    }
}
