package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SexOffenderHTML extends AppCompatActivity {
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sexoffender_html);

        wv=findViewById(R.id.wv2);

        //웹의 설정객체를 얻어오기
        WebSettings settings=wv.getSettings();
        settings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient());

        wv.setWebChromeClient(new WebChromeClient());

        //웹뷰가 보여줄 웹문서 (.html) 로드하기
        wv.loadUrl("https://www.sexoffender.go.kr/");
    }
}