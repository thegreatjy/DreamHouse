package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

//커뮤니티 글 작성
public class Community_write extends AppCompatActivity {

    // 글 제목
    EditText et_title;
    // 글 내용
    EditText et_contents;
    // 카테고리
    String category;
    // 작성자
    String user_id;
    // 글 id
    int index ;
    JSONArray peoples = null;
    private static final String TAG_RESULTS = "result";
    String myJSON_num;
    JSONArray community_array = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        //글 번호 불러오기
        getData("http://118.67.131.208/community_num.php");

        // 글 제목
        et_title = (EditText)findViewById(R.id.title);
        // 글 내용
        et_contents = (EditText)findViewById(R.id.content);
        // 카테고리
        // 위에 저장되어있음 category

        // 작성자
        try {
            user_id = URLEncoder.encode(MainActivity.userID, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Spinner객체 생성
        final Spinner spinner_field = (Spinner) findViewById(R.id.spinner);

        //1번에서 생성한 field.xml의 item을 String 배열로 가져오기
        String[] str = getResources().getStringArray(R.array.my_array);

        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, R.layout.spinner_item,str);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        //spinner 이벤트 리스너
        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = "" + parent.getItemAtPosition(position);
                //Toast.makeText(getApplication(), category, Toast.LENGTH_SHORT).show();

                if(category.equals("팝니다")){
                    //Toast.makeText(getApplication(), "팝니다", Toast.LENGTH_SHORT).show();
                    et_contents.setText("팔고자 하는 것에 대해 입력해주세요.\n\n어떤 것을?: \n가격: \n상태: ");
                } else if(category.equals("구합니다")){
                    //Toast.makeText(getApplication(), "구합니다", Toast.LENGTH_SHORT).show();
                    et_contents.setText("구하고자 하는 것에 대해 입력해주세요.\n\n어떤 것을?: ");
                } else {
                    et_contents.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });






        //등록 버튼 이벤트 ====================>>>>> php 작성 필요(디비에 저장하기)
        Button button_upload = (Button)findViewById(R.id.button_upload);
        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title=et_title.getText().toString();
                final String contents=et_contents.getText().toString();

                //id 개수 읽어오기
                index = community_count();

                //Toast.makeText( getApplicationContext(), "테스트 성공1", Toast.LENGTH_SHORT ).show();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response ); //필요없음, JSONException 때문에

                            Toast.makeText( getApplicationContext(), "글이 등록되었습니다.", Toast.LENGTH_SHORT ).show();


                            Intent intent = new Intent(Community_write.this, Community.class);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText( getApplicationContext(), "테스트 실패1", Toast.LENGTH_SHORT ).show();
                        } catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText( getApplicationContext(), "테스트 실패2", Toast.LENGTH_SHORT ).show();
                        }
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                CommunityRequest CommunityRequest=new CommunityRequest(++index, user_id, title, category, contents, responseListener);
                RequestQueue queue= Volley.newRequestQueue(Community_write.this);
                queue.add(CommunityRequest);
            }
        });
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON_num = result;
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public int community_count() {
        int num = -1;

        try {
            JSONObject jsonObj = new JSONObject(myJSON_num);
            community_array = jsonObj.getJSONArray("result");

            num = community_array.length();
            //Toast.makeText( getApplicationContext(), Integer.toString(num), Toast.LENGTH_SHORT ).show();

        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText( getApplicationContext(), "테스트 실패3", Toast.LENGTH_SHORT ).show();
        }
        return num;
    }

}