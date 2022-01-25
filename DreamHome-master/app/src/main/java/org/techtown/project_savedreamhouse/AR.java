package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

//AR 어플로 이동하기 위한 코드
public class AR extends AppCompatActivity {
    private Intent intent;
    public String packageName = "com.DefaultCompany.argps"; // 유니티 AR 앱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
        ImageButton imageARButton = (ImageButton) findViewById(R.id.icon);
        imageARButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AR.this.startActivity(intent);
            }
        });
    }
}