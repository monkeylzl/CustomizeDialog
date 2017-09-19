package com.example.lzl.customizedialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
