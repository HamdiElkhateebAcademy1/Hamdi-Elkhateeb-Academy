package com.hamdiacademy.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    private WebView webView;
    private ValueCallback<Uri[]> uploadCallback;
    private static final int FILE_CHOOSER = 1001;

    private static final String URL =
            "https://hamdielkhateebacademy1.github.io/Hamdi-Elkhateeb-Academy/?app=1";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        webView = new WebView(this);
        webView.setLayoutParams(
                new ViewGroup.LayoutParams(-1, -1)
        );

        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setSupportZoom(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                String url = request.getUrl().toString();

                if (url.startsWith("http://")
                        || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return true;
                }

                try {
                    startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    );
                } catch (Exception ignored) {
                }

                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {

                if (uploadCallback != null) {
                    uploadCallback.onReceiveValue(null);
                }

                uploadCallback = callback;

                Intent intent = params.createIntent();

                try {
                    startActivityForResult(intent, FILE_CHOOSER);
                } catch (Exception e) {
                    uploadCallback = null;
                    return false;
                }

                return true;
            }
        });

        if (state == null) {
            webView.loadUrl(URL);
        } else {
            webView.restoreState(state);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        if (requestCode == FILE_CHOOSER
                && uploadCallback != null) {

            Uri[] result =
                    WebChromeClient.FileChooserParams
                            .parseResult(resultCode, data);

            uploadCallback.onReceiveValue(result);
            uploadCallback = null;
        }

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (uploadCallback != null) {
            uploadCallback.onReceiveValue(null);
        }

        webView.destroy();
        super.onDestroy();
    }
}
