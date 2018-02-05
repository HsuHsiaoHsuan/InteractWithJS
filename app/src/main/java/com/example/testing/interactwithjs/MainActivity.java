package com.example.testing.interactwithjs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

// http://blog.csdn.net/carson_ho/article/details/64904691

public class MainActivity extends AppCompatActivity {

    private static final String JS_NAME = "JsTest";
    private Toolbar mToolbar;
    private Button bCallJs;
    private WebView webView;
    private boolean lockBackKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        bCallJs = findViewById(R.id.callJs);
        bCallJs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:calledFromAndroid('msg from Android Java')");
                    }
                });

            }
        });

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new JsInterface(this), JS_NAME);
        webView.loadUrl("file:///android_asset/jsinteract.html");
    }

    @Override
    public void onBackPressed() {
        if (lockBackKey) {
            Toast.makeText(this, "Back key was locked", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    class JsInterface {
        private Context mContext;
        JsInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void showToolbar(final boolean showIt) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToolbar.setVisibility(showIt ? View.VISIBLE : View.GONE);
                }
            });
        }

        @JavascriptInterface
        public void openAndroidDialog(String title, String msg) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(msg)
                    .setTitle(title);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @JavascriptInterface
        public void lockBackKey(boolean lockIt) {
            lockBackKey = lockIt;
        }
    }
}
