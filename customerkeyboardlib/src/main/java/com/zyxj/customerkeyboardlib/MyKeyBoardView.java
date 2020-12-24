package com.zyxj.customerkeyboardlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.List;

class MyKeyBoardView extends KeyboardView {
    private Context mContext;
    private Keyboard mKeyBoard;
    private Paint confirmBtnTextPaint = new Paint();
    private Paint normalBtnTextPaint = new Paint();
    //    private Typeface fontType = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
    private boolean clickEffectEnable = true;//点击变色效果打开还是关闭

    public MyKeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyKeyBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initPaint(confirmBtnTextPaint, Color.WHITE);
        initPaint(normalBtnTextPaint, Color.BLACK);
    }

    private void initPaint(Paint paint, int textColor) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        setMBFont(paint);
    }

    //RobotoMedium
//    private void setRMFont(Paint paint) {
//        paint.setTypeface(fontType);
//    }

    //中黑体
    private void setMBFont(Paint paint) {
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setClickEffectEnable(boolean clickEffectEnable) {
        this.clickEffectEnable = clickEffectEnable;
    }

    /**
     * 重新画一些按键，添加 super.onDraw(canvas);后父类会处理一些复杂绘制情况，简化我们子类的逻辑，但是这种情况，我们画的新按键会覆盖在父类画的按键上面，而不是替换掉父类的,可以设置keyboardview的 backGround和 text 颜色为透明来处理叠加问题
     * 由于我们的键盘ui比较简单，所以直接覆盖父类方法处理
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mKeyBoard = this.getKeyboard();
        List<Key> keys = null;
        if (mKeyBoard != null) {
            keys = mKeyBoard.getKeys();
        }

        int confirmDrawable = clickEffectEnable ? R.drawable.keyboard_selector_confirm : R.drawable.keyboard_blue;
        int delDrawable = clickEffectEnable ? R.drawable.keyboard_selector_del : R.drawable.keyboard_del;
        int numDrawable = clickEffectEnable ? R.drawable.keyboard_selector_normal : R.drawable.keyboard_white;

        if (keys != null) {
            for (Key key : keys) {
                if (key.codes[0] == -4) {//确定按钮
                    drawKeyBackground(confirmDrawable, canvas, key);
                    drawText(canvas, key, confirmBtnTextPaint);
                } else if (key.codes[0] == -5) {//删除按钮
                    drawKeyBackground(delDrawable, canvas, key);
                    drawText(canvas, key, normalBtnTextPaint);
                } else if (key.codes[0] == -3) {
                    drawKeyBackground(delDrawable, canvas, key);
                    drawText(canvas, key, normalBtnTextPaint);
                } else {//其他按钮
                    drawKeyBackground(numDrawable, canvas, key);
                    drawText(canvas, key, normalBtnTextPaint);
                }
            }
        }
    }

    private void drawKeyBackground(int drawableId, Canvas canvas, Key key) {
        Drawable npd = mContext.getResources().getDrawable(
                drawableId);
        int[] drawableState = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            npd.setState(drawableState);
        }
        //获取keyboardView的padding值，计算位置时需要加padding，key的x y值是不包含padding从0开始计算的。
        int paddingTop = this.getPaddingTop();
        int top = key.y + paddingTop;
        int bottom = top + key.height;
        npd.setBounds(key.x, top, key.x + key.width, bottom);
        npd.draw(canvas);
    }

    private void drawText(Canvas canvas, Key key, Paint paint) {
        int paddingTop = this.getPaddingTop();//要加上padding值
        Rect bounds = new Rect();
        if (key.label != null) {
            String label = key.label.toString();
            int keyTextSize;
            if (label.length() > 1 && key.codes.length < 2) {//确定等自定义按钮，可查看父类处理方式
                keyTextSize = getLabelTextSize();
            } else {//数字类按钮
                keyTextSize = getKeyTextSize();
            }
            paint.setTextSize(keyTextSize);
            //把文字的尺寸信息放入bounds
            paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
            //文字底部y值
            int textBaseY = key.y + paddingTop + key.height / 2 + bounds.height() / 2;
            canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                    textBaseY, paint);
        } else if (key.icon != null) {
            int top = key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + paddingTop;
            key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, top,
                    key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), top + key.icon.getIntrinsicHeight());
            key.icon.draw(canvas);
        }
    }

    private int getKeyTextSize() {
        return UnitTransform.dp2px(mContext, 18);
//        return getTextSizeByReflect("mKeyTextSize");//反射拿不到会导致键盘数字空白，直接写死
    }

    private int getLabelTextSize() {
        return UnitTransform.dp2px(mContext, 20);
//        return getTextSizeByReflect("mLabelTextSize");
    }

    private int getTextSizeByReflect(String fieldName) {
        Field field;
        int labelTextSize = 0;
        try {
            field = KeyboardView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            labelTextSize = (int) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return labelTextSize;
    }
}
