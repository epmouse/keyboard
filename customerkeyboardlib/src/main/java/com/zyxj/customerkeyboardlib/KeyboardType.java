package com.zyxj.customerkeyboardlib;

public enum KeyboardType {
    BIG_CONFIRM_BUTTON(R.xml.keyboard_number, true),
    LEFT_DOWN_HIDE_BIG_CONFIRM(R.xml.keyboard_number_1, true),
    LEFT_DOWN_HIDE_RIGHT_DEL(R.xml.keyboard_number_2, true),
    LEFT_DOWN_NULL_RIGHT_DEL(R.xml.keyboard_number_3, true),
    LEFT_DOWN_X_ID_CARD(R.xml.keyboard_number_idcard_x, true),
    KEYBOARD_ABC(R.xml.keyboard_abc, false);

    private final int keyboardXml;
    //标识当前键盘样式为数字键盘
    private final boolean isNumKeyboard;

    KeyboardType(int xml, boolean isNumKeyboard) {
        this.keyboardXml = xml;
        this.isNumKeyboard = isNumKeyboard;
    }

    public int getKeyboardXml() {
        return keyboardXml;
    }

    public boolean isNumKeyboard() {
        return isNumKeyboard;
    }
}
