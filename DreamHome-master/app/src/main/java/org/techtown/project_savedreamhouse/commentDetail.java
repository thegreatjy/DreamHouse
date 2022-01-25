package org.techtown.project_savedreamhouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//커뮤니티 전체 댓글 보기
public class commentDetail extends AppCompatActivity {
    private ListView listView;
    String myJSON;
    ArrayList<HashMap<String, String>> personList;
    //글의 id를 가져올 idList
    private List<Post> idList;
    private static final String TAG_RESULTS = "result";

    String TAG_user_id = "user_id";
    String TAG_contents = "contents";

    String real_title;
    String user_id;
    String contents;

    JSONArray peoples = null;

    int postID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        postID = getIntent().getIntExtra("articleID", 0);

        listView = (ListView)findViewById(R.id.listView_comment);
        personList = new ArrayList<HashMap<String, String>>();

        idList=new ArrayList<Post>();

        try {
            personList.clear();
            myCommunity("http://118.67.131.208/comment_select.php?articleID="+postID);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    //글목록 보여주기([카테고리]제목, 작성자)
    protected void showList(){
        try {
            JSONObject jsonObject = new JSONObject( myJSON );
            peoples = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i=0; i<peoples.length(); i++){
                JSONObject c = peoples.getJSONObject(i);

                user_id = c.getString("writerID");
                contents = c.getString("contents");

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_user_id, user_id);
                persons.put(TAG_contents, contents);

                personList.add(persons);

            }

            ListAdapter adapter = new SimpleAdapter(
                    commentDetail.this, personList, R.layout.list_comment,
                    new String[]{TAG_user_id, TAG_contents},
                    new int[]{R.id.comment_writer, R.id.comment_Contentss}
            ){
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);

                    return view;
                }
            };
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText( getApplicationContext(), "실패2", Toast.LENGTH_SHORT ).show();
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText( getApplicationContext(), "실패3", Toast.LENGTH_SHORT ).show();
        }
    }


    // 데이터 가져오기
    public void myCommunity(String url) {
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
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}
