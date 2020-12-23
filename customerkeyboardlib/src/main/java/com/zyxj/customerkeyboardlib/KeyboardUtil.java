package com.zyxj.customerkeyboardlib;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class KeyboardUtil {
    private Activity mActivity;
    private boolean isRandom;

    private KeyboardView mKeyboardView;
    private Keyboard mKeyboardNumber;//数字键盘
    private EditText mEditText;

    private OnOkClick mOnOkClick;
    private onCancelClick mOnCancelClick;

    KeyboardUtil(Activity activity, KeyboardView mKeyboardView) {
        this.mActivity = activity;
        this.mKeyboardView = mKeyboardView;
    }

    /**
     * 是否是随机键盘
     * 只会对数字键进行随机
     */
    void setWhetherRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    /**
     * @param editText     需要绑定自定义键盘的edittext
     * @param keyboardType 键盘样式
     *                     BIG_CONFIRM_BUTTON  右侧大的确定按钮样式
     *                     LEFT_DOWN_HIDE_BIG_CONFIRM   右侧大的确定按钮 左下角为隐藏图标
     */
    void attachTo(EditText editText, KeyboardType keyboardType) {
        this.mEditText = editText;
        Utils.hideSystemSofeKeyboard(mActivity.getApplicationContext(), mEditText);
        showSoftKeyboard(keyboardType);
    }

    void setCurrentEditText(EditText editText) {
        this.mEditText = editText;
        Utils.hideSystemSofeKeyboard(mActivity, editText);
    }

    void showSoftKeyboard(KeyboardType keyboardType) {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = new Keyboard(mActivity, keyboardType.getKeyboardXml());
        }
        if (mKeyboardView == null) {
            Toast.makeText(mActivity.getApplicationContext(), "软键盘初始化失败，请重试！", Toast.LENGTH_LONG).show();
            return;
        }
        //只有当前键盘样式是数字键盘时，isRandom参数才生效
        if (isRandom && keyboardType.isNumKeyboard()) {
            randomKeyboardNumber();
        } else {
            mKeyboardView.setKeyboard(mKeyboardNumber);
        }
        mKeyboardView.setEnabled(true);
        //点击时放大显示，如果设为true需要适配处理，否则放大出来的是空白框，没有按键文字
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            VibrateUtils.vSimple(mActivity, 30);
            Editable editable = mEditText.getText();
            int start = mEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 隐藏键盘
                if (mOnCancelClick != null) {
                    mOnCancelClick.onCancelClick();
                }
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {// 隐藏键盘
                if (mOnOkClick != null) {
                    mOnOkClick.onOkClick();
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    interface OnOkClick {
        void onOkClick();
    }

    interface onCancelClick {
        void onCancelClick();
    }

    /**
     * @param onOkClick 不设置监听则执行默认操作（关闭键盘）
     */
    void setOnOkClick(OnOkClick onOkClick) {
        mOnOkClick = onOkClick;
    }

    void setOnCancelClick(onCancelClick onCancelClick) {
        mOnCancelClick = onCancelClick;
    }


    private boolean isNumber(String str) {
        String wordstr = "0123456789";
        return wordstr.contains(str);
    }

    private void randomKeyboardNumber() {
        List<Keyboard.Key> keyList = mKeyboardNumber.getKeys();
        // 查找出0-9的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isNumber(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(),
                    temp.get(num).getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i)
                    .getCode();
        }

        mKeyboardView.setKeyboard(mKeyboardNumber);
    }
}