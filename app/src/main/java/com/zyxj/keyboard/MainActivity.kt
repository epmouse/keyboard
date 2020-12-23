package com.zyxj.keyboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import com.zyxj.customerkeyboardlib.IKeyboardManager
import com.zyxj.customerkeyboardlib.KeyboardManager
import com.zyxj.customerkeyboardlib.KeyboardPopWindow
import com.zyxj.customerkeyboardlib.KeyboardType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var keyboardManager: IKeyboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initKeyboard()
    }


    @SuppressLint("ClickableViewAccessibility")
    fun initKeyboard() {
        editText.setOnTouchListener { v, _ ->
            keyboardManager.showInputMethod(v as EditText?)
            false
        }
        keyboardManager = KeyboardManager.Builder(this)
            .setKeyboardDialog(KeyboardPopWindow(this))
            .setKeyboardType(KeyboardType.LEFT_DOWN_HIDE_BIG_CONFIRM)
            .setClickEventStatus(true)//按键点击变色效果，颜色变深
            .setTopView(null)//传入一个view，它会显示到键盘顶部，随键盘一起显示。
            .setWhetherRandom(false)// 设置是否是按键随机键盘，目前只有数字键盘才支持随机
            .setRootView(null)//当键盘在非Activity和Fragment上弹出时（比如在一个dialog上弹出键盘），设置当前布局的根view，用来处理点击键盘外部键盘消失，如果不需要支持点击外部消失则无需理会
            .setOutsideTouchable(true)//是否点击外部消失,默认为true
//            .setIgnoreViewIds(R.id.xxx, R.id.xxx, R.id.xxx)//如果你想让用户点击键盘外某些view键盘不消失，请把该view的id传入，这个只有在setOutsideTouchable(true/false)设置true的时候起作用
            .setOnKeyboardShowListener {//键盘弹出的监听

            }.setOnKeyboardDismissListener {//键盘隐藏的监听

            }.build()


    }
}