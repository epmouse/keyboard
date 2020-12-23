package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class _KeyboardPopWindow extends PopupWindow implements IKeyboardDialog {

    private KeyboardUtil keyboardUtil;
    private Context context;
    private KeyboardView keyboardView;
    private EditText editText;
    private View contentView;
    private LinearLayout llKeyboardTopView;
    private boolean isRandom;
    private ViewGroup rootView;

    public _KeyboardPopWindow(Context context) {
        this(context, null);
    }

    /**
     * @param context  Activity
     * @param rootView  popUpWindow.showAtLocation(View parent, int gravity, int x, int y) 中的parent
     */
    public _KeyboardPopWindow(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;
        initView();
    }

    protected void initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_keyboard_customer, null);
        keyboardView = contentView.findViewById(R.id.keyboard_customer);
        llKeyboardTopView = contentView.findViewById(R.id.ll_keyboard_top_view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(contentView);
        setAnimationStyle(R.style.BorrowKeyboardAnimation);
    }

    //从外部传入顶部view。
    public void setTopCustomerView(View view) {
        if (view != null && llKeyboardTopView != null) {
            //改为build模式创建manager后，每次的view是不销毁的，所以需要先remove
            llKeyboardTopView.removeView(view);
            llKeyboardTopView.addView(view);
        }
    }

    @Override
    public int[] getKeyboardViewIds() {
        return new int[0];
    }


    @Override
    public void setCurrentEditText(EditText editText) {
        this.editText = editText;
        if (keyboardUtil != null) {
            keyboardUtil.setCurrentEditText(editText);
        }
    }

    @Override
    public void setClickEventEnable(boolean clickEventEnable) {
        if (keyboardView instanceof MyKeyBoardView) {
            ((MyKeyBoardView) keyboardView).setClickEffectEnable(clickEventEnable);
        }
    }

    @Override
    public void setWhetherRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    /**
     * show方法必需在activity加载完成后调用，建议在view.post方法中调用。
     */
    @Override
    public void show(KeyboardType keyboardType) {
        showAtLocation(getPageRootView(), Gravity.BOTTOM, 0, 0);
        initKeyboard(keyboardType);
    }

    private ViewGroup getPageRootView() {
        if (rootView != null)
            return rootView;
        View childAt = ((ViewGroup) ((Activity)context).findViewById(android.R.id.content)).getChildAt(0);
        if (childAt instanceof ViewGroup) {
            return (ViewGroup) childAt;
        }
        return new LinearLayout(context);
    }

    private void initKeyboard(KeyboardType keyboardType) {
        keyboardUtil = new KeyboardUtil((Activity) context, keyboardView);
        keyboardUtil.setWhetherRandom(isRandom);
        keyboardUtil.setOnOkClick(new KeyboardUtil.OnOkClick() {
            @Override
            public void onOkClick() {
                dismiss();
            }
        });
        keyboardUtil.setOnCancelClick(new KeyboardUtil.onCancelClick() {
            @Override
            public void onCancelClick() {
                dismiss();
            }
        });
        keyboardUtil.attachTo(editText, keyboardType);
    }
}
