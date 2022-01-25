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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//커뮤니티 화면
public class Community extends AppCompatActivity {

    private ListView listView;
    String myJSON;
    ArrayList<HashMap<String, String>> personList;
    //글의 id를 가져올 idList
    private List<Post> idList;
    private static final String TAG_RESULTS = "result";

    String TAG_index = "id";
    String TAG_user_id = "user_id";
    String TAG_title = "title";
    String TAG_category = "category";
    String TAG_contents = "contents";

    String real_title;
    String user_id;
    String contents;


    JSONArray peoples = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);


        listView = (ListView)findViewById(R.id.listView_community);
        personList = new ArrayList<HashMap<String, String>>();

        idList=new ArrayList<Post>();

        try {
            personList.clear();
            myCommunity("http://118.67.131.208/community_load.php");

        } catch (Exception e) {
            e.printStackTrace();
        }
        //글쓰기 버튼 작성
        Button button_write = (Button)findViewById(R.id.button_write);
        button_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Community_write.class);
                startActivity(intent);
            }
        });

    }



    //글목록 보여주기([카테고리]제목, 작성자)
    protected void showList(){
        try {
            JSONObject jsonObject = new JSONObject( myJSON );
            peoples = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i=0; i<peoples.length(); i++){
                JSONObject c = peoples.getJSONObject(i);
                int index = c.getInt("id");
                //System.out.println(index);
                user_id = c.getString("user_id");
                real_title = "[" + c.getString("category") + "]" + c.getString("title");
                contents = c.getString("contents");

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_user_id, user_id);
                persons.put(TAG_title, real_title);


                Post post = new Post(index);
                idList.add(post);
                personList.add(persons);

            }

            ListAdapter adapter = new SimpleAdapter(
                    Community.this, personList, R.layout.list_community,
                    new String[]{TAG_user_id, TAG_title},
                    new int[]{R.id.writer, R.id.title2}
            ){
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);
                    LinearLayout list_community = (LinearLayout)view.findViewById(R.id.list_community);
                    list_community.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            //Toast.makeText( getApplicationContext(), "성공", Toast.LENGTH_SHORT ).show();
                            //System.out.println(idList.get(position).getId());
                            Intent intent = new Intent(Community.this, CommunityDetail.class);
                            intent.putExtra("id", idList.get(position).getId());//id
                            startActivity(intent);
                        }
                    });
                    return view;
                }
            };
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText( getApplicationContext(), "실패2", Toast.LENGTH_SHORT ).show();
        } catch(Exception e){
            e.printStackTrace();
            //Toast.makeText( getApplicationContext(), "실패3", Toast.LENGTH_SHORT ).show();
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