package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//로그인
public class LoginActivity extends AppCompatActivity {
    private EditText et_id, et_pass;
    private Button btn_login,btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id=findViewById(R.id.editText_id);
        et_pass=findViewById(R.id.editText_passwd);
        btn_login=findViewById(R.id.button_login);
        btn_register=findViewById(R.id.button_tojoin);

        Button button_find_id = findViewById(R.id.button_find_id);
        Button button_find_passwd = findViewById(R.id.button_find_passwd);

        // 아이디 찾기 버튼 눌렀을 때
        button_find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindId.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 버튼 눌렀을 때
        button_find_passwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), FindPasswd.class);
                startActivity(intent2);
            }
        });

        //회원가입 버튼 눌렀을 때
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼 눌렀을 때
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID=et_id.getText().toString();
                String userPass=et_pass.getText().toString();


                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject=new JSONObject(response);
                            boolean success=jasonObject.getBoolean("success");
                            if (success) {//회원등록 성공한 경우
                                String userID = jasonObject.getString("id");
                                String userPass = jasonObject.getString("pw");
                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("pw", userPass);
                                intent.putExtra("id", userID);
                                startActivity(intent);
                            }


                            else{//회원등록 실패한 경우
                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest=new LoginRequest(userID,userPass,responseListener);
                RequestQueue queue= Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}
