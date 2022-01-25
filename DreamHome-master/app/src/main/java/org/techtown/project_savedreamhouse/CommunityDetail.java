package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//커뮤니티 글 읽기(불러오기)
public class CommunityDetail extends AppCompatActivity {
    public static int postid;
    public String user_id;
    //String postid_str;

    String myJSON;
    JSONArray post = null;
    private static final String TAG_RESULTS = "result";

    private static final String TAG_title = "title";  //제목
    private static final String TAG_writer = "user_id"; //작성자 id
    private static final String TAG_category = "category"; //카테고리
    private static final String TAG_cotents = "contents"; //내용

    TextView detail_title;
    TextView detail_writer;
    TextView detail_contents;
    // 글 내용
    EditText comment_contents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        //String postid_str = intent.getExtras().getString("id");

        postid = getIntent().getIntExtra("id", 0);
        try{
            user_id= URLEncoder.encode(MainActivity.userID, "UTF-8");
        }catch(Exception e){

        }




        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_writer = (TextView) findViewById(R.id.detail_writer);
        detail_contents = (TextView) findViewById(R.id.detail_contents);

        getData("http://118.67.131.208/PHP_CommunityDetail.php", postid);

        //댓글 내용
        comment_contents = (EditText)findViewById(R.id.comment);

        //댓글 등록
        Button button_comment_register = (Button)findViewById(R.id.comment_register);
        button_comment_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str_comment_contents=comment_contents.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject( response ); //필요없음, JSONException 때문에

                            Toast.makeText( getApplicationContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT ).show();

                            Intent intent = new Intent(CommunityDetail.this, commentDetail.class);
                            intent.putExtra("articleID", postid);       //현재 글 id(postid)를 넘겨줌
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
                commentRequest commentRequest=new commentRequest(postid, user_id, str_comment_contents, responseListener);
                RequestQueue queue= Volley.newRequestQueue(CommunityDetail.this);
                queue.add(commentRequest);
            }
        });

        //댓글 보기
        Button button_comment_view = (Button)findViewById(R.id.comment_view);
        button_comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(postid);
                Intent intent = new Intent(CommunityDetail.this, commentDetail.class);
                intent.putExtra("articleID", postid);       //현재 글 id(postid)를 넘겨줌
                startActivity(intent);
            }
        });
    }

    public void getData(String url, int search) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String search_query = "SEARCH="+search;

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //버튼 누르면 검색 추가
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outputstrim = con.getOutputStream();
                    outputstrim.write(search_query.getBytes("UTF-8"));
                    outputstrim.flush();
                    outputstrim.close();


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

                myJSON = result;

                String title;
                String writer;
                String category;
                String contents;
                String real_title;

                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    post = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < post.length(); i++) {
                        JSONObject c = post.getJSONObject(i);

                        title = c.getString(TAG_title);
                        writer = c.getString(TAG_writer);
                        category = c.getString(TAG_category);
                        contents = c.getString(TAG_cotents);
                        real_title = "[" + category + "] " + title;


                        detail_title.setText(real_title);
                        detail_writer.setText(writer);
                        detail_contents.setText(contents);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*Toast toast = Toast.makeText(getApplicationContext(),A,Toast.LENGTH_LONG);
                toast.show();*/
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}