package org.techtown.project_savedreamhouse;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

//범죄발생수 확인하는 html로 이동
public class crimeHTML extends AppCompatActivity {
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crimehtml);

        wv=findViewById(R.id.wv);

        WebSettings settings=wv.getSettings();
        settings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new WebChromeClient());

        wv.loadUrl("https://kkapstone.github.io/Crime/WebContent/testtest.html"); //에셋 주소 : /android_asset
    }

}