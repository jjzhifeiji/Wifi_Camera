package com.joyhonest.sports_camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.net.MailTo;
public class PrivacyPolicyActivity extends Activity {
    private ImageView backPrivacy;
    private TextView btnReload;
    private RelativeLayout errorLayout;
    private ProgressBar loadProgressBar;
    private boolean loadSuccess = true;
    private WebView webView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_privacy_policy);
        initView();
    }

    private void initView() {
        this.webView = (WebView) findViewById(R.id.webView);
        this.loadProgressBar = (ProgressBar) findViewById(R.id.loadProgressBar);
        this.errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        this.btnReload = (TextView) findViewById(R.id.btn_reload);
        this.backPrivacy = (ImageView) findViewById(R.id.backPrivacy);
        this.webView.getSettings().setJavaScriptEnabled(true);
        loadWebpage();
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
                if (Build.VERSION.SDK_INT >= 23) {
                    return;
                }
                loadSuccess = false;
                handleLoadSuccess(false);
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (webResourceRequest.isForMainFrame()) {
                    loadSuccess = false;
                    handleLoadSuccess(false);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                PrivacyPolicyActivity privacyPolicyActivity = PrivacyPolicyActivity.this;
                privacyPolicyActivity.handleLoadSuccess(privacyPolicyActivity.loadSuccess);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str.startsWith(MailTo.MAILTO_SCHEME)) {
                    sendMail(str);
                    return true;
                }
                return super.shouldOverrideUrlLoading(webView, str);
            }
        });
        this.btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWebpage();
            }
        });
        this.backPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void sendMail(String str) {
        Intent intent = new Intent("android.intent.action.SENDTO");
        intent.setData(Uri.parse(str));
        intent.putExtra("android.intent.extra.EMAIL", new String[]{str.substring(7)});
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void loadWebpage() {
        this.loadSuccess = true;
        this.webView.loadUrl("http://www.joyhonest.com/Privacy_policy_Sports_Camera.html");
        this.loadProgressBar.setVisibility(View.VISIBLE);
        this.webView.setVisibility(View.GONE);
        this.errorLayout.setVisibility(View.GONE);
    }



    public void handleLoadSuccess(boolean z) {
        this.loadProgressBar.setVisibility(View.GONE);
        if (z) {
            this.webView.setVisibility(View.VISIBLE);
            this.errorLayout.setVisibility(View.GONE);
        } else {
            this.webView.setVisibility(View.GONE);
            this.errorLayout.setVisibility(View.VISIBLE);
        }
        if (z) {
            if ((getResources().getConfiguration().uiMode & 48) == 32) {
                this.webView.evaluateJavascript("javascript:setMode('dark')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String str) {
                    }
                });
            } else {
                this.webView.evaluateJavascript("javascript:setMode('light')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String str) {
                    }
                });
            }
        }
    }
}
