package org.techtown.project_savedreamhouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//관심 매물 편의 순위

//매물과 제일 가까운 시설까지의 거리를 구하여 점수로 사용한다.
//시설(예. 편의점)과 제일 가까운 매물의 순위가 가장 높다.

//동점인 매물은 가격이 저렴한 매물을 상위로 올린다.

public class Compare_c  extends AppCompatActivity {
    int index=1;
    String myJSON_cnv;
    String myJSON_rank;

    String myJSON;  //관심매물

    JSONArray ranks = null;
    JSONArray response=null;
    JSONArray response2 = null;
    JSONArray level_1=null;
    JSONArray level_2=null;
    JSONArray level_3=null;

    private  static final String estate="estate";
    private  static final String distance="distance";

    int estate_id;
    int previous_estate_id;

    int atclNo;
    int prc;
    int rentPrc;

    int score;// distance로 점수주기
    double total_score=0;// 총 점수 계산하기
    double geori;
    int a;
    double c;
    ArrayList<HashMap<String, String>> houserankList;
    ListView ranklist;
    //key 정렬되는 treemap(큰게 위로)
    Comparator<Integer> comparator = (s1, s2)->s1.compareTo(s2);

    //매물 주변 안전시설(아래의 4개) 개수 카운트할 때 필요한 맵
    LinkedHashMap<Integer, Integer> ranking_cnv = new LinkedHashMap<>();

    //최종 편의 순위
    ArrayList<Integer> rank=new ArrayList<>();
    Map<Integer, Double> ranking= new HashMap<Integer, Double>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_c);
        houserankList = new ArrayList<HashMap<String, String>>();
        ranklist = (ListView)findViewById(R.id.listView_convenience);

        try {
            loadJSON("http://118.67.131.208/nearBusStop.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "convenience");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //각 관심 매물의 최종 안전 순위(rank)를 정한다.
        //final_ranking();
    }
    protected void ranking(String myJSON, String php_response, LinkedHashMap<Integer, Integer> resultMap) {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            response = jsonObj.getJSONArray(php_response);
            for(int i=0; i<response.length(); i++){
                JSONArray c1 = response.getJSONArray(i);
                total_score=0;
                for(int j=0; j<c1.length(); j++){
                    JSONObject c2 = c1.getJSONObject(j);
                    a = c2.getInt("estate");
                    c = c2.getDouble("distance");
                    total_score=total_score+c;
                }
                /*System.out.println(a);
                System.out.println("one estate total_distance: "+total_score);*/
                ranking.put(a, total_score);
            }
            ranking = sorting(1,ranking);
            for (Map.Entry<Integer, Double> entry : ranking.entrySet()) {
                /*System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());*/
                houserankList.clear();
                loadJSON("http://118.67.131.208/compare_c.php?HID=" + entry.getKey(), "rank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadJSON(String url, String type) {
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
                //안전 시설마다 다른 json에 저장
                //안전 시설의 개수를 사용하여 순위를 매긴다.
                if(type.equals("convenience")){
                    myJSON_cnv = result;
                    ranking(myJSON_cnv, "response", ranking_cnv);
                } else if(type.equals("cart")){
                    myJSON = result;
                } else if(type.equals("rank")){
                    myJSON_rank = result;
                    rnk_the_list();
                }

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    public LinkedHashMap<Integer, Double> sorting(int i, Map<Integer, Double>m){
        List<Integer> keySetList = new ArrayList<>(m.keySet());
        LinkedHashMap<Integer, Double> resultMap= new LinkedHashMap<>();
        //내림차순 정렬
        if(i==0){
            // 내림차순 //
            Collections.sort(keySetList, (o1, o2) -> (m.get(o2).compareTo(m.get(o1))));
        }else if(i==1){
            //오름차순 정렬
            Collections.sort(keySetList, (o1, o2) -> (m.get(o1).compareTo(m.get(o2))));
        }
        for(Integer key : keySetList) {
            resultMap.put(key, m.get(key));
        }
        return resultMap;
    }

    protected void rnk_the_list(){
        try {
            JSONObject obj = new JSONObject(myJSON_rank);
            ranks = obj.getJSONArray("result");
            int count=0;

            int houseID;
            String houseGudong;
            String houseSort;
            String housePrice;
            String houseRent;
            String houseRoom;
            String houseAtcl;
            String houseFloor;
            String houseSpace1;
            String houseSpace2;
            String houseDetail;

            while(count<ranks.length()){
                JSONObject object=ranks.getJSONObject(count);
                houseID = object.getInt("atclNo");
                System.out.println(houseID+"캡스톤");
                houseGudong = "서울시 " + object.getString("search_area");
                houseSort = object.getString("tradTpNm")+" ";
                housePrice = object.getString("hanPrc");
                houseRent = "/"+object.getString("rentPrc");
                houseRoom = object.getString("rletTpNm");
                houseAtcl = " · "+object.getString("atclNm");
                houseFloor = object.getString("flrInfo")+"층";
                houseSpace1 = " "+object.getString("spc1")+"/";
                houseSpace2 = " "+object.getString("spc2")+"평";
                houseDetail = object.getString("atclFetrDesc");

                HashMap<String, String> houseranks = new HashMap<String, String>();
                houseranks.put("num",Integer.toString(index));
                houseranks.put("search_area",houseGudong);
                houseranks.put("tradTpNm",houseSort);
                houseranks.put("hanPrc",housePrice);
                houseranks.put("rentPrc",houseRent);
                houseranks.put("rletTpNm",houseRoom);
                houseranks.put("atclNm",houseAtcl);
                houseranks.put("flrInfo",houseFloor);
                houseranks.put("spc1",houseSpace1);
                houseranks.put("spc2",houseSpace2);
                houseranks.put("atclFetrDesc",houseDetail);

                houserankList.add(houseranks);
                count++;
                index++;
            }
            ListAdapter adapter = new SimpleAdapter(
                    Compare_c.this, houserankList, R.layout.list_compare,
                    new String[]{"num","tradTpNm","hanPrc","rentPrc","rletTpNm","atclNm","flrInfo","spc1","atclFetrDesc"},
                    new int[]{R.id.num, R.id.deal_type, R.id.deposit, R.id.monthly_rent, R.id.building_type, R.id.building_name, R.id.floor, R.id.space, R.id.detail_info})
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);
                    LinearLayout linear = (LinearLayout)view.findViewById(R.id.linear);
                    linear.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                        }
                    });
                    return view;
                }
            };
            ranklist.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}