package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

//관심 매물에 있는 각 매물에 대한 체크리스트를 다루는 코드
public class CheckList extends AppCompatActivity {

    String user_id;
    String sale_id_input;
    int sale_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        // 작성자
        try {
            user_id = URLEncoder.encode(MainActivity.userID, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        sale_id_input = intent.getExtras().getString("houseID");
        sale_id = Integer.parseInt(sale_id_input);

        RadioGroup radiogroup1 = (RadioGroup)findViewById(R.id.radiogroup1);
        RadioGroup radiogroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        RadioGroup radiogroup3 = (RadioGroup)findViewById(R.id.radiogroup3);
        RadioGroup radiogroup4 = (RadioGroup)findViewById(R.id.radiogroup4);
        RadioGroup radiogroup5 = (RadioGroup)findViewById(R.id.radiogroup5);
        RadioGroup radiogroup6 = (RadioGroup)findViewById(R.id.radiogroup6);
        RadioGroup radiogroup7 = (RadioGroup)findViewById(R.id.radiogroup7);
        RadioGroup radiogroup8 = (RadioGroup)findViewById(R.id.radiogroup8);
        RadioGroup radiogroup9 = (RadioGroup)findViewById(R.id.radiogroup9);
        RadioGroup radiogroup10 = (RadioGroup)findViewById(R.id.radiogroup10);
        RadioGroup radiogroup11 = (RadioGroup)findViewById(R.id.radiogroup11);
        RadioGroup radiogroup12 = (RadioGroup)findViewById(R.id.radiogroup12);
        RadioGroup radiogroup13 = (RadioGroup)findViewById(R.id.radiogroup13);
        RadioGroup radiogroup14 = (RadioGroup)findViewById(R.id.radiogroup14);
        RadioGroup radiogroup15 = (RadioGroup)findViewById(R.id.radiogroup15);
        RadioGroup radiogroup16 = (RadioGroup)findViewById(R.id.radiogroup16);
        RadioGroup radiogroup17 = (RadioGroup)findViewById(R.id.radiogroup17);
        RadioGroup radiogroup18 = (RadioGroup)findViewById(R.id.radiogroup18);
        RadioGroup radiogroup19 = (RadioGroup)findViewById(R.id.radiogroup19);

        //체크리스트 불러오기 버튼을 눌렀을 때
        Button button_load = (Button)findViewById(R.id.load);
        button_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {
                                int q1 = jsonObject.getInt("q1");
                                int q2 = jsonObject.getInt("q2");
                                int q3 = jsonObject.getInt("q3");
                                int q4 = jsonObject.getInt("q4");
                                int q5 = jsonObject.getInt("q5");
                                int q6 = jsonObject.getInt("q6");
                                int q7 = jsonObject.getInt("q7");
                                int q8 = jsonObject.getInt("q8");
                                int q9 = jsonObject.getInt("q9");
                                int q10 = jsonObject.getInt("q10");
                                int q11 = jsonObject.getInt("q11");
                                int q12 = jsonObject.getInt("q12");
                                int q13 = jsonObject.getInt("q13");
                                int q14 = jsonObject.getInt("q14");
                                int q15 = jsonObject.getInt("q15");
                                int q16 = jsonObject.getInt("q16");
                                int q17 = jsonObject.getInt("q17");
                                int q18 = jsonObject.getInt("q18");
                                int q19 = jsonObject.getInt("q19");



                                radiogroup1.check(q1);
                                radiogroup2.check(q2);
                                radiogroup3.check(q3);
                                radiogroup4.check(q4);
                                radiogroup5.check(q5);
                                radiogroup6.check(q6);
                                radiogroup7.check(q7);
                                radiogroup8.check(q8);
                                radiogroup9.check(q9);
                                radiogroup10.check(q10);
                                radiogroup11.check(q11);
                                radiogroup12.check(q12);
                                radiogroup13.check(q13);
                                radiogroup14.check(q14);
                                radiogroup15.check(q15);
                                radiogroup16.check(q16);
                                radiogroup17.check(q17);
                                radiogroup18.check(q18);
                                radiogroup19.check(q19);


                                Toast.makeText( getApplicationContext(), "체크리스트 불러오기 성공", Toast.LENGTH_SHORT ).show();
                                //Intent intent = new Intent( signInActivity.this );// LoginActivity.class
                                //startActivity( intent );

                                //체크리스트 저장 실패시
                            } else {
                                //Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText( getApplicationContext(), "실패2", Toast.LENGTH_SHORT ).show();
                        } catch(Exception e){
                            e.printStackTrace();
                            //Toast.makeText( getApplicationContext(), "실패3", Toast.LENGTH_SHORT ).show();
                        }
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                CheckListLoad CheckListLoad=new CheckListLoad(user_id, sale_id, responseListener);
                RequestQueue queue= Volley.newRequestQueue(CheckList.this);
                queue.add(CheckListLoad);
            }
        });

        // 저장 버튼을 눌렀을 때
        Button button_save = (Button)findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton rb1 = (RadioButton)findViewById(radiogroup1.getCheckedRadioButtonId());
                RadioButton rb2 = (RadioButton)findViewById(radiogroup2.getCheckedRadioButtonId());
                RadioButton rb3 = (RadioButton)findViewById(radiogroup3.getCheckedRadioButtonId());
                RadioButton rb4 = (RadioButton)findViewById(radiogroup4.getCheckedRadioButtonId());
                RadioButton rb5 = (RadioButton)findViewById(radiogroup5.getCheckedRadioButtonId());
                RadioButton rb6 = (RadioButton)findViewById(radiogroup6.getCheckedRadioButtonId());
                RadioButton rb7 = (RadioButton)findViewById(radiogroup7.getCheckedRadioButtonId());
                RadioButton rb8 = (RadioButton)findViewById(radiogroup8.getCheckedRadioButtonId());
                RadioButton rb9 = (RadioButton)findViewById(radiogroup9.getCheckedRadioButtonId());
                RadioButton rb10 = (RadioButton)findViewById(radiogroup10.getCheckedRadioButtonId());
                RadioButton rb11 = (RadioButton)findViewById(radiogroup11.getCheckedRadioButtonId());
                RadioButton rb12 = (RadioButton)findViewById(radiogroup12.getCheckedRadioButtonId());
                RadioButton rb13 = (RadioButton)findViewById(radiogroup13.getCheckedRadioButtonId());
                RadioButton rb14= (RadioButton)findViewById(radiogroup14.getCheckedRadioButtonId());
                RadioButton rb15 = (RadioButton)findViewById(radiogroup15.getCheckedRadioButtonId());
                RadioButton rb16 = (RadioButton)findViewById(radiogroup16.getCheckedRadioButtonId());
                RadioButton rb17 = (RadioButton)findViewById(radiogroup17.getCheckedRadioButtonId());
                RadioButton rb18 = (RadioButton)findViewById(radiogroup18.getCheckedRadioButtonId());
                RadioButton rb19 = (RadioButton)findViewById(radiogroup19.getCheckedRadioButtonId());

                Integer ob1 = new Integer(radiogroup1.getCheckedRadioButtonId());
                Integer ob2 = new Integer(radiogroup2.getCheckedRadioButtonId());
                Integer ob3 = new Integer(radiogroup3.getCheckedRadioButtonId());
                Integer ob4 = new Integer(radiogroup4.getCheckedRadioButtonId());
                Integer ob5 = new Integer(radiogroup5.getCheckedRadioButtonId());
                Integer ob6 = new Integer(radiogroup6.getCheckedRadioButtonId());
                Integer ob7 = new Integer(radiogroup7.getCheckedRadioButtonId());
                Integer ob8 = new Integer(radiogroup8.getCheckedRadioButtonId());
                Integer ob9 = new Integer(radiogroup9.getCheckedRadioButtonId());
                Integer ob10 = new Integer(radiogroup10.getCheckedRadioButtonId());
                Integer ob11 = new Integer(radiogroup11.getCheckedRadioButtonId());
                Integer ob12 = new Integer(radiogroup12.getCheckedRadioButtonId());
                Integer ob13 = new Integer(radiogroup13.getCheckedRadioButtonId());
                Integer ob14 = new Integer(radiogroup14.getCheckedRadioButtonId());
                Integer ob15 = new Integer(radiogroup15.getCheckedRadioButtonId());
                Integer ob16 = new Integer(radiogroup16.getCheckedRadioButtonId());
                Integer ob17 = new Integer(radiogroup17.getCheckedRadioButtonId());
                Integer ob18 = new Integer(radiogroup18.getCheckedRadioButtonId());
                Integer ob19 = new Integer(radiogroup19.getCheckedRadioButtonId());

                // 유효성 검사하기
                if(ob1.equals(-1) || ob2.equals(-1) || ob3.equals(-1) || ob4.equals(-1) || ob5.equals(-1) ||
                        ob6.equals(-1) || ob7.equals(-1) || ob8.equals(-1) || ob9.equals(-1) || ob10.equals(-1) ||
                        ob11.equals(-1) || ob12.equals(-1) || ob13.equals(-1) || ob14.equals(-1) || ob15.equals(-1) ||
                        ob16.equals(-1) || ob17.equals(-1) || ob18.equals(-1) || ob19.equals(-1)) {
                    Toast.makeText(CheckList.this, "체크리스트를 모두 체크하세요", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    int q1 = rb1.getId();
                    int q2 = rb2.getId();
                    int q3 = rb3.getId();
                    int q4 = rb4.getId();
                    int q5 = rb5.getId();
                    int q6 = rb6.getId();
                    int q7 = rb7.getId();
                    int q8 = rb8.getId();
                    int q9 = rb9.getId();
                    int q10 = rb10.getId();
                    int q11 = rb11.getId();
                    int q12 = rb12.getId();
                    int q13 = rb13.getId();
                    int q14 = rb14.getId();
                    int q15 = rb15.getId();
                    int q16 = rb16.getId();
                    int q17 = rb17.getId();
                    int q18 = rb18.getId();
                    int q19 = rb19.getId();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject( response );
                                boolean success = jsonObject.getBoolean( "success" );

                                //회원가입 성공시
                                if(success) {
                                    Toast.makeText( getApplicationContext(), "체크리스트가 저장되었습니다.", Toast.LENGTH_SHORT ).show();
                                    //Intent intent = new Intent( signInActivity.this );// LoginActivity.class
                                    //startActivity( intent );

                                    //체크리스트 저장 실패시
                                } else {
                                    Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    //서버로 volley를 이용해서 요청을 함
                    CheckListRequest CheckListRequest=new CheckListRequest(user_id, sale_id, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16, q17, q18, q19,responseListener);
                    RequestQueue queue= Volley.newRequestQueue(CheckList.this);
                    queue.add(CheckListRequest);
                }

            }
        });
    }
}