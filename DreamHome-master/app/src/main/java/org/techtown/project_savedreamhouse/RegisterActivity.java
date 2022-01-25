package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

//회원가입
public class RegisterActivity extends AppCompatActivity {
    private EditText et_id, et_pass, et_passck, et_name, et_phone, et_email, et_gender;
    private RadioButton gender;
    private Button btn_register, validateIdButton;
    private AlertDialog dialog;
    private boolean validate=false;
    String join_passwd_check;
    String verification_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        //아이디값 찾아주기
        et_id = findViewById( R.id.editText_id );
        et_pass = findViewById( R.id.editText_passwd );
        //et_passck=findViewById(R.id.editText_passwd_check);
        et_name = findViewById( R.id.editText_name );
        et_phone = findViewById( R.id.editText_phone );
        et_email = findViewById( R.id.editText_email );
        //et_gender=findViewById(R.id.et_gender);

        EditText editText_phone = findViewById(R.id.editText_phone);
        //EditText editText_verification_code = findViewById(R.id.editText_verification_code);
        EditText editText_passwd_check = findViewById(R.id.editText_passwd_check);

        validateIdButton=findViewById(R.id.button_validate_id);

        //id 중복체크
        validateIdButton.setOnClickListener(new View.OnClickListener() {//id중복체크
            @Override
            public void onClick(View view) {
                String userID=et_id.getText().toString();
                if(validate)
                {
                    return;
                }
                if(userID.equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                    dialog=builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                                dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                et_id.setEnabled(false);
                                validate=true;
                                validateIdButton.setText("확인");
                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder( RegisterActivity.this );
                                dialog=builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인",null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateIdRequest validateRequest=new ValidateIdRequest(userID,responseListener);
                RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);

            }
        });

        //회원가입, 회원가입 버튼 클릭 시 수행
        btn_register = findViewById( R.id.button_join );
        btn_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = et_id.getText().toString();
                String userPass = et_pass.getText().toString();
                String userName = et_name.getText().toString();
                String userPhone = et_phone.getText().toString();
                String userEmail = et_email.getText().toString();
                String join_passwd_check = editText_passwd_check.getText().toString();
                //int userGender = Integer.parseInt( et_gender.getText().toString() );
                int userGender =1;


                //verification_code = editText_verification_code.getText().toString();

                // 모두 입력했는지 검사
                if(userID.isEmpty() || userPass.isEmpty() || userName.isEmpty() || userPhone.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "필수 사항을 모두 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // 모두 입력 됐음
                    // 아이디 중복 검사(데이터베이스에서 중복 확인)


                    // 비밀번호 유효성 검사(8자리 이상 영문자, 숫자, 특수문자 포함)
                    if(!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,20}.$", userPass))
                    {
                        Toast.makeText(RegisterActivity.this,"비밀번호가 형식에 맞지 않습니다.",Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 비밀번호 확인 후 다르면 토스트 메세지 띄움
                    if(!(userPass.equals(join_passwd_check))){
                        Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 휴대폰 번호 인증하기(아임포트?다날?)

                    // 조건 전부 만족하면 회원가입 완료 토스트 메세지 띄우고 1.5초 뒤 회원가입 창 닫기
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run(){
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("id", userID);
                            startActivity(intent);
                        }
                    }, 1500);
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //회원가입 성공시
                            if(success) {

                                Toast.makeText( getApplicationContext(), "성공", Toast.LENGTH_SHORT ).show();
                                //Intent intent = new Intent( signInActivity.this );// LoginActivity.class
                                //startActivity( intent );

                                //회원가입 실패시
                            } else {
                                Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                RegisterRequest registerRequest=new RegisterRequest(userID,userPass, userName, userPhone,userEmail,userGender,responseListener);
                RequestQueue queue= Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}