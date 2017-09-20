# CustomizeDialog
自定义具有圆角的 Dialog

[自定义Dialog的详细步骤（实现自定义样式一般原理）](http://blog.csdn.net/oqihaogongyuan/article/details/50958659)

#### 权限（Webview）
src/main/AndroidManifest.xml

```
<uses-permission android:name="android.permission.INTERNET"/>
<!-- 浮窗所需权限-->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
```

#### 主要的布局：

这里面嵌套了一个 Webview，用以加载制定的资源。要想拥有圆角效果，关键在于`android:background="@drawable/disagree_background"`,通过自定义的 background 实现背景的填充和圆角的实现

src/main/res/layout/MyDialog.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_centerInParent="true"
                android:layout_gravity="center"
                android:layout_marginBottom="90dp"
                android:layout_marginLeft="16dp"
                android:layout_marginRight="16dp"
                android:layout_marginTop="90dp"
                android:fitsSystemWindows="false"
                android:orientation="vertical">

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/dialog_background"
        android:fitsSystemWindows="false">


        <WebView
            android:id="@+id/webview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginLeft="12dp"
            android:layout_marginRight="12dp"
            android:background="@null"
            android:gravity="center">
        </WebView>


        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="40dp"
            android:layout_alignParentBottom="true"
            android:layout_alignParentLeft="true"
            android:layout_alignParentStart="true"
            android:orientation="horizontal">

            <Button
                android:id="@+id/disagree"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_centerVertical="true"
                android:layout_weight="1"
                android:background="@drawable/disagree_background"
                android:gravity="center"
                android:text="不同意"
                android:textColor="#2692ff"
                android:textSize="15dp"/>

            <Button
                android:id="@+id/agree"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_centerVertical="true"
                android:layout_toRightOf="@+id/disagree"
                android:layout_weight="1"
                android:background="@drawable/agree_background"
                android:gravity="center"
                android:text="同意"
                android:textColor="#ffffff"
                android:textSize="15dp"/>

        </LinearLayout>

    </RelativeLayout>

</RelativeLayout>
```

#### 主题styles：

自定义自己的主题模式，注意`parent="android:style/Theme.Dialog"`,可以在 AS 中通过 ctrl+B 跳转查看包含哪些属性，以及还有哪些主题

src/main/res/values/MyDialog.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!--自定义dialog背景全透明无边框theme -->
    <style name="MyDialog" parent="android:style/Theme.Dialog">

        <item name="android:windowFrame">@android:color/transparent</item>
        <!-- 边框 -->
        <item name="android:windowIsFloating">true</item>
        <!-- 是否浮现在activity之上 -->
        <item name="android:windowIsTranslucent">true</item>
        <!-- 半透明 -->
        <item name="android:windowNoTitle">true</item>
        <!-- 无标题 -->
        <item name="android:background">@android:color/transparent</item>
        <!-- 背景色 -->
        <item name="android:windowBackground">@android:color/transparent</item>
        <!-- 背景透明 -->
        <item name="android:backgroundDimEnabled">true</item>
        <!-- 模糊 -->
        <item name="android:windowFullscreen">false</item>
        <!-- 全屏 -->
        <item name="android:backgroundDimAmount">0.5</item>

    </style>

</resources>
```

#### 圆角：

主要进行了不同布局的圆角的设置，注意对于底层的两个 agree／disagree button 的圆角设置，只进行了四个角中的不同角的圆角化，不必整个框全部圆角化。

* src/main/res/drawable/dialog_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"

    android:shape="rectangle">
    <!-- 填充的颜色 -->
    <solid android:color="#ffffff"/>
    <!-- 设置按钮的四个角为弧形 -->
    <!-- android:radius 弧形的半径 -->
    <corners android:radius="10dp"/>

</shape>
```

* src/main/res/drawable/agree_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"

    android:shape="rectangle">
    <!-- 填充的颜色 -->
    <solid android:color="#2692ff"/>
    <!-- 设置按钮的四个角为弧形 -->
    <!-- android:radius 弧形的半径 -->
    <corners
        android:bottomRightRadius="10dp"/>

    <stroke
        android:width="0.5dp"
        android:color="#2692ff"/>

</shape>
```

* src/main/res/drawable/disagree_background.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
    xmlns:android="http://schemas.android.com/apk/res/android"

    android:shape="rectangle">
    <!-- 填充的颜色 -->
    <solid android:color="#ffffff"/>
    <!-- 设置按钮的四个角为弧形 -->
    <!-- android:radius 弧形的半径 -->
    <corners
        android:bottomLeftRadius="10dp"/>

    <stroke
        android:width="0.5dp"
        android:color="#ffffff"/>

</shape>

```

#### 主要逻辑类：

主要封装了两个button的点击侦听，提供其他类使用，进行逻辑操作。

src/main/java/MyDialog.class
```java
/**
 * Created by lzl on 2017/9/16.
 */

public class MyDialog extends Dialog {

    private static final String TAG = "CustomizeDialog";
    private Button agree;
    private Button disagree;
    private WebView webView;
    private onAgreeOnclickListener agreeOnclickListener;
    private onDisagreeOnclickListener disagreeOnclickListener;

    public MyDialog(Context context) {
        super(context, R.style.MyDialog);
    }
    //侦听back键
    private OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };
    /**
     * 设置取消按钮的显示内容和监听
     */
    public void setAgreeOnclickListener(onAgreeOnclickListener onAgreeOnclickListener) {
        this.agreeOnclickListener = onAgreeOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     */
    public void setDisagreeOnclickListener(onDisagreeOnclickListener onDisagreeOnclickListener) {
        this.disagreeOnclickListener = onDisagreeOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        agree = (Button)findViewById(R.id.agree);
        disagree = (Button)findViewById(R.id.disagree);
        webView = (WebView)findViewById(R.id.webview);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.6F;
        layoutParams.flags = layoutParams.flags | LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(layoutParams);
        //设置按back键 MyDialog不消失
        setOnKeyListener(keylistener);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化Webview
        webviewInit();
        //初始化界面控件的事件
        initEvent();
        Log.e(TAG, "Dialog init");

    }
    /**
     * Webview 加载本地的 html(src/main/assets/xxxx.html) 资源，也可以加载网络资源(https://www.baidu.com/)
     */
    private void webviewInit() {
        webView.loadUrl("http://www.baidu.com/");
        webView.setWebViewClient(new WebViewClient());
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeOnclickListener != null) {
                    agreeOnclickListener.onAgreeClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disagreeOnclickListener != null) {
                    disagreeOnclickListener.onDisagreeClick();
                }
            }
        });
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onAgreeOnclickListener {
        public void onAgreeClick();
    }

    public interface onDisagreeOnclickListener {
        public void onDisagreeClick();
    }
}
```

#### 自定义布局的应用：

整个布局已经完全的设计好了，并将侦听抛出，然后就是真正的应用。

这里主要的逻辑涉及到几个点：

* 真对于 Google 渠道才弹出此对话框
* 首次运行才弹出
* 点击同意，退出Dialog，才进行 onCreate() 方法中其他操作,并且下次运行 APP 不再弹出，直接运行
* 点击不同意，退出整个 APP，并下次运行继续弹出此 Dialog
* 弹出 Dialog 后，back 键失效，只能点击同意或者不同意

src/main/java/MainActivity.class
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkFirstRun();
    }

    //Google PID
    //private static final String GOOGLE_PID = "xxxx";
    private MyDialog mDialog;

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun ) {
            //隐私规定提示弹出 Dialog
            mDialog = new MyDialog(this);
            mDialog.show();
            mDialog.setAgreeOnclickListener(new MyDialog.onAgreeOnclickListener() {
                @Override
                public void onAgreeClick() {
                    mDialog.dismiss();
                    /**
                     * 其他的方法
                     */
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isFirstRun", false)
                        .apply();
                }
            });

            mDialog.setDisagreeOnclickListener(new MyDialog.onDisagreeOnclickListener() {
                @Override
                public void onDisagreeClick() {
                    android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                    System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isFirstRun", true)
                        .apply();
                }
            });
        } else {
            /**
             * 其他的方法
             */
        }
    }
}
```

有时半透明的黑色背景并不能展现，由于逻辑的复杂性，导致最终的 theme 的效果没有显示出来，这样为了确保它能够出现的关键,就是直接进行代码级别的再次情调属性参数，以确保能够能够正确显示自己的自定义效果（其实所有的效果都可以通过这种方式显示，这样会更加稳定，这时就可以完全的省略掉 styles.xml/layout.xml 一些资源的配置）

```java
WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
layoutParams.dimAmount = 0.6F;
layoutParams.flags = layoutParams.flags | LayoutParams.FLAG_DIM_BEHIND;
mDialog.getWindow().setAttributes(layoutParams);
```
最终的效果图为：


<div align=center>
![](http://oo2m64upw.bkt.clouddn.com/17-9-16/55325813.jpg?imageView/3/w/200/h/300 )
</div>
