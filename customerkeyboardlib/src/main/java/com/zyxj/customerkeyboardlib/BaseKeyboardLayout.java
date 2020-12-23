package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 直接展示键盘，不依附于dialog
 */
public class BaseKeyboardLayout implements IKeyboardDialog {
    private static final String TAG = "BaseKeyboardLayout";

    private KeyboardUtil keyboardUtil;
    protected Activity activity;
    private KeyboardView keyboardView;
    private EditText editText;
    private View contentView;
    private LinearLayout topLayout;
    private boolean isRandom;
    private ViewGroup rootView;

    public BaseKeyboardLayout(@NonNull Activity activity) {
        this(activity, null);
    }

    /**
     * @param activity
     * @param rootView 传入键盘要依附的跟布局，适配非Activity页面，比如在dialog中弹出
     */
    public BaseKeyboardLayout(@NonNull Activity activity, ViewGroup rootView) {
        this.activity = activity;
        this.rootView = rootView;
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_keyboard_customer, null);
        //头布局的问题： 当头布局上没有设置点击事件时，点击事件会被页面的的跟布局捕获，从而出现用户点击头布局区域会导致键盘隐藏的问题，此处给键盘跟布局布局设置click事件处理
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        keyboardView = contentView.findViewById(R.id.keyboard_customer);
        topLayout = contentView.findViewById(R.id.ll_keyboard_top_view);
        setContentView(contentView);
    }

    /**
     * 可重写此方法改造成依附于dialog的模式
     *
     * @param contentView
     */
    protected void setContentView(View contentView) {
        ViewGroup pageRootView = getPageRootView();
        pageRootView.addView(contentView);
    }

    protected ViewGroup getPageRootView() {
        if (rootView != null)
            return rootView;
        View childAt = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        if (childAt instanceof ViewGroup) {
            return (ViewGroup) childAt;
        }
        return new LinearLayout(activity);
    }

    @Override
    public void show(KeyboardType keyboardType) {
        initKeyboard(keyboardType);
        contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        contentView.setVisibility(View.GONE);
    }

    @Override
    public boolean isShowing() {
        return contentView != null && View.VISIBLE == contentView.getVisibility() && contentView.isShown();
    }

    @Override
    public void setClickEventEnable(boolean clickEventEnable) {
        if (keyboardView != null) {
            ((MyKeyBoardView) keyboardView).setClickEffectEnable(clickEventEnable);
        }
    }

    @Override
    public void setWhetherRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    @Override
    public void setTopCustomerView(View view) {
        if (view != null && topLayout != null) {
            //改为build模式创建manager后，每次的view是不销毁的，所以需要先remove
            topLayout.removeView(view);
            topLayout.addView(view);
        }
    }

    @Override
    public void setCurrentEditText(EditText editText) {
        this.editText = editText;
        if (keyboardUtil != null) {
            keyboardUtil.setCurrentEditText(editText);
        }
    }

    @Override
    public int[] getKeyboardViewIds() {
        List<Integer> ids = new ArrayList<>();
        ids.add(contentView.getId());//把跟布局也加进去排除
        getViewIds(ids, contentView);
        int[] arr = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            arr[i] = ids.get(i);
        }
        return arr;
    }

    /**
     * 遍历获取键盘上所有view的id
     */
    private void getViewIds(List<Integer> ids, View view) {
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ((ViewGroup) view).getChildAt(i);
                ids.add(childAt.getId());
                Utils.idLog(activity, TAG, childAt);
                if (childAt instanceof ViewGroup) {
                    getViewIds(ids, childAt);
                }
            }
        }
    }

    protected void initKeyboard(KeyboardType keyboardType) {
        keyboardUtil = new KeyboardUtil(activity, keyboardView);
        keyboardUtil.setWhetherRandom(isRandom);
        keyboardUtil.setOnOkClick(new KeyboardUtil.OnOkClick() {
            @Override
            public void onOkClick() {
                if (!onKeyOkClick()) {
                    dismiss();
                }
            }
        });
        keyboardUtil.setOnCancelClick(new KeyboardUtil.onCancelClick() {
            @Override
            public void onCancelClick() {
                if (!onKeyCancelClick()) {
                    dismiss();
                }
            }
        });
        keyboardUtil.attachTo(editText, keyboardType);
    }

    /**
     * 重写此方法处理点击键盘 隐藏按钮的逻辑
     *
     * @return 返回true则覆盖父类默认操作（隐藏键盘）
     */
    protected boolean onKeyCancelClick() {
        return false;
    }

    /**
     * 重写此方法处理点击键盘 确定按钮的逻辑
     *
     * @return 返回true则覆盖父类默认操作（隐藏键盘）
     */
    protected boolean onKeyOkClick() {
        return false;
    }


}
