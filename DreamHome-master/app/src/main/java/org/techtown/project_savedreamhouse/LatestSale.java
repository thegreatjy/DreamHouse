package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//최근 본 방
public class LatestSale extends AppCompatActivity {

    // 최근 본 방 변수
    public String myJSON;
    public JSONArray response=null;
    ArrayList<HashMap<String, String>> personList;
    private List<House> houseList; //houseID가져올 houseList

    ListView list;
    String user_id;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_houseNo = "atclNo";
    private static final String TAG_houseID = "houseID";
    private static final String TAG_tradTpNm = "tradTpNm";
    private static final String TAG_hanPrc = "hanPrc";
    private static final String TAG_rentPrc = "rentPrc";
    private static final String TAG_search_area = "search_area";
    private static final String TAG_atclNm = "atclNm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_sale);

        try {
            user_id = URLEncoder.encode(MainActivity.userID, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 최근 본 방
        list = (ListView) findViewById(R.id.listView_lately);
        personList = new ArrayList<HashMap<String, String>>();
        houseList=new ArrayList<House>();

        personList.clear();
        houseList.clear();

        loadJSON("http://118.67.131.208/recentEstates_select.php?userID="+user_id);

    }

    //showList에 해당함
    protected void loadData(String myJSON) {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            response = jsonObj.getJSONArray("result");
            //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();

            int count = 0;
            //Toast.makeText(getApplicationContext(), "개수 :" + response.length(), Toast.LENGTH_SHORT).show();

            int HID=0;
            String area;
            String name;
            int rentPrc;
            String hanPrc;
            String trad;

            while (count < response.length()) {
                JSONObject p = response.getJSONObject(count);
                HID=p.getInt("houseID");
                area=p.getString("search_area");
                name=p.getString("atclNm");
                rentPrc=p.getInt("rentPrc");
                hanPrc=p.getString("hanPrc")+"/";
                trad=p.getString("tradTpNm");

                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_houseID, Integer.toString(HID));
                persons.put(TAG_search_area, area);
                persons.put(TAG_atclNm, name);
                persons.put(TAG_rentPrc, Integer.toString(rentPrc));
                persons.put(TAG_hanPrc, hanPrc);
                persons.put(TAG_tradTpNm, trad);

                House house = new House(Integer.toString(HID));
                houseList.add(house);
                personList.add(persons);

                count++;
            }
            ListAdapter adapter = new SimpleAdapter(
                    LatestSale.this, personList, R.layout.list_latest,
                    new String[]{TAG_tradTpNm, TAG_hanPrc,TAG_rentPrc,TAG_search_area,TAG_atclNm},
                    new int[]{R.id.tradTpNm,R.id.hanPrc,R.id.comment_writer_id,R.id.search_area,R.id.atclNm})
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);
                    LinearLayout layout_latest = (LinearLayout)view.findViewById(R.id.layout_latest);

                    layout_latest.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SaleDetail.class);
                            intent.putExtra("houseID", houseList.get(position).getHouseID());
                            startActivity(intent);
                        }

                    });
                    return view;
                }
            };
            list.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
        }
    }

    //최근 본 방 데이터 가져오기
    public void loadJSON(String url) {
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
                loadData(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}