该工具类提供了两种软键盘弹出方式：
1、keyboardPopWindow：dialog形式，使用popUpWindow处理，可以直接依附在页面底部，类似于系统键盘；
2、BaseKeyboardLayout模式： view形式，此方式软键盘就是一个普通的view，依附在弹出页面的跟布局里，弹出位置跟你的页面布局有关系，。

两种方式优劣：分别用1 和 2 代表
1. 优势： 可以根据输入框位置 折叠页面（需要页面有scrollview支持）
   劣势：由于实现原理是把键盘布局addView加入了页面的根布局中 ，所以键盘的弹出位置需要用户自己控制，不会默认在底部。  并且无弹出动画
2. 优势：因为是popWindow形式，所以能保证总是在页面顶层，并且是底部弹出,用户不需要考虑适配。自带动画
   劣势：无法自动折叠页面布局。

```
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
            .setKeyboardDialog(KeyboardManager.createDialogKeyboard(this))//createDialogKeyboard 软键盘为dialog形式，createViewKeyboard，软键盘为view形式
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
```