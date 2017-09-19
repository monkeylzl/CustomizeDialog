package com.example.lzl.customizedialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

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