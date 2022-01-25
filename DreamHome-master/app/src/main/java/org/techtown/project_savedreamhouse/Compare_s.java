package org.techtown.project_savedreamhouse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.util.Map;
import java.util.TreeMap;

//관심 매물 안전 순위

//시설이 매물 근방 600m에 있으면 1등급
//시설이 매물 근방 600m~1200m에 있으면 2등급
//시설이 매물 근방 1200m~1800m에 있으면 3등급, 없어도 3등급
//같은 등급내에서는 시설의 개수로 순위를 매긴다.

//같은 등급, 근처에 있는 시설의 개수가 같아서 동점이라면 매물 가격이 낮은 매물을 상위로 올린다.

public class Compare_s  extends AppCompatActivity {
    int index=1;
    String myJSON_police;
    String myJSON_delivery;
    String myJSON_safehouse;
    String myJSON_cctv;
    String myJSON_rank;

    String myJSON;  //관심매물

    JSONArray ranks = null;
    JSONArray response=null;
    JSONArray level_1=null;
    JSONArray level_2=null;
    JSONArray level_3=null;

    JSONArray response_cartList=null;

    private  static final String estate="estate";
    private  static final String distance="distance";

    int estate_id;
    int previous_estate_id;

    int atclNo;
    int prc;
    int rentPrc;

    private Button compare_convenience;
    ArrayList<HashMap<String, String>> houserankList;
    ListView ranklist;
    //key 정렬되는 treemap(큰게 위로)
    Comparator<Integer> comparator = (s1, s2)->s1.compareTo(s2);
    Map<Integer, Integer> temp = new TreeMap<>(comparator);

    //매물 주변 안전시설(아래의 4개) 개수 카운트할 때 필요한 맵
    LinkedHashMap<Integer, Integer> ranking_police = new LinkedHashMap<>();
    LinkedHashMap<Integer, Integer> ranking_delivery = new LinkedHashMap<>();
    LinkedHashMap<Integer, Integer> ranking_safehouse = new LinkedHashMap<>();
    LinkedHashMap<Integer, Integer> ranking_cctv = new LinkedHashMap<>();
    //최종 안전 순위
    ArrayList<Integer> rank=new ArrayList<>();
    Map<Integer, Integer> ranking= new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_s);
        houserankList = new ArrayList<HashMap<String, String>>();
        ranklist = (ListView)findViewById(R.id.listView_safety);
        try {
            //cartList.clear();
            loadJSON("http://118.67.131.208/CartList.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "cart");
            //관심 매물의 주변 안전 시설 개수를 세어 각 안전시설의 순위(ranking_~~)를 정한다.
            loadJSON("http://118.67.131.208/nearPolice.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "police");
            loadJSON("http://118.67.131.208/nearDelivery.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "delivery");
            loadJSON("http://118.67.131.208/nearSafehouse.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "safehouse");
            loadJSON("http://118.67.131.208/nearCctv.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"), "cctv");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //각 관심 매물의 최종 안전 순위(rank)를 정한다.
        //final_ranking();
    }

    protected void ranking(String myJSON, String php_response, LinkedHashMap<Integer, Integer>resultMap) {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            response = jsonObj.getJSONArray(php_response);

            int count=0;

            while(count< response.length()){
                JSONObject p = response.getJSONObject(count);

                if(count==0){
                    System.out.println("count : "+count);
                    level_1=p.getJSONArray("level_1");

                    int count_police=0;
                    for(int i=0;i<level_1.length();i++){
                        JSONObject p_600 = level_1.getJSONObject(i);
                        estate_id = p_600.getInt(estate);
                        //dis = p_1800.getDouble(distance);

                        if(i==0){
                            //맨 첫번째 매물 -> 매물 아이디 list에 id push
                            count_police++;
                        }
                        else if(i!=0 && (estate_id!=previous_estate_id)){
                            //600 배열의 맨 첫 번째가 아니고, 매물이(매물 id) 바뀌면
                            temp.put(previous_estate_id, count_police);
                            count_police=1;
                        }
                        else if(previous_estate_id==estate_id){
                            //매물 아이디가 전의 매물 아이디와 동일하면
                            count_police++;
                        }

                        if((i+1)==level_1.length()){
                            //response_police_1800의 마지막 요소이면  넣고 종료
                            //count_police++;
                            temp.put(estate_id, count_police);
                        }

                        previous_estate_id=estate_id;
                    }
                    //temp를 ranking_police에 넣는다.
                    System.out.println("===level 1===");
                    temp=sorting(0, temp);
                    for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
                        resultMap.put(entry.getKey(), entry.getValue());

                        System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());
                    }
                    //temp 비우기
                    temp.clear();

                }else if(count==1){
                    System.out.println("count : "+count);
                    level_2=p.getJSONArray("level_2");

                    int count_police=0;
                    for(int i=0;i<level_2.length();i++){
                        JSONObject p_1200 = level_2.getJSONObject(i);
                        estate_id = p_1200.getInt(estate);
                        //dis = p_1800.getDouble(distance);

                        if(i==0){
                            //맨 첫번째 매물 -> 매물 아이디 list에 id push
                            count_police++;
                        }
                        else if(i!=0 && (estate_id!=previous_estate_id)){
                            //600 배열의 맨 첫번째가 아니고, 매물이(매물 id) 바뀌면
                            temp.put(previous_estate_id, count_police);
                            count_police=1;
                        }
                        else if(previous_estate_id==estate_id){
                            //매물 아이디가 전의 매물 아이디와 동일하면
                            count_police++;
                        }

                        if((i+1)==level_2.length()){
                            //response_police_1800의 마지막 요소이면  넣고 종료
                            //count_police++;
                            temp.put(estate_id, count_police);
                        }
                        previous_estate_id=estate_id;
                    }
                    //temp를 ranking_police에 넣는다.
                    System.out.println("===level 2===");
                    temp=sorting(0, temp);
                    for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {

                        resultMap.put(entry.getKey(), entry.getValue());

                        System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());
                    }
                    //temp 비우기
                    temp.clear();

                }else if(count==2){
                    System.out.println("count : "+count);
                    level_3=p.getJSONArray("level_3");

                    int count_police=0;
                    for(int i=0;i<level_3.length();i++){
                        JSONObject p_1800 = level_3.getJSONObject(i);
                        estate_id = p_1800.getInt(estate);
                        //dis = p_1800.getDouble(distance);

                        if(i==0){
                            //맨 첫번째 매물 -> 매물 아이디 list에 id push
                            count_police++;
                            //첫번째이자 마지막일 경우
                            temp.put(estate_id, count_police);
                        }
                        else if(i!=0 && (estate_id!=previous_estate_id)){
                            //1800 배열의 맨 첫번째가 아니고, 매물이(매물 id) 바뀌면
                            temp.put(previous_estate_id, count_police);
                            count_police=1;
                        }
                        else if(previous_estate_id==estate_id){
                            //매물 아이디가 전의 매물 아이디와 동일하면
                            count_police++;
                        }

                        if((i+1)==level_3.length()){
                            //response_police_1800의 마지막 요소이면  넣고 종료
                            //count_police++;
                            temp.put(estate_id, count_police);
                        }
                        previous_estate_id=estate_id;
                    }
                    //temp를 ranking_police에 넣는다.
                    System.out.println("===level 3===");
                    temp=sorting(0, temp);
                    for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
                        resultMap.put(entry.getKey(), entry.getValue());

                        System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());
                    }
                    //temp 비우기
                    temp.clear();
                }
                count++;
            }
            System.out.println("===final rank==="+php_response);
            for (Map.Entry<Integer, Integer> entry : resultMap.entrySet()) {
                System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());
            }
            //주변에 안전시설이 없는 관심 매물을 resultMap에 넣어줌
            compareCart(resultMap);
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
                if(type.equals("police")){
                    myJSON_police = result;
                    ranking(myJSON_police, "response_police", ranking_police);
                }else if(type.equals("delivery")){
                    myJSON_delivery = result;
                    ranking(myJSON_delivery, "response_delivery", ranking_delivery);
                }else if(type.equals("safehouse")){
                    myJSON_safehouse = result;
                    ranking(myJSON_safehouse, "response_safehouse", ranking_safehouse);
                }else if(type.equals("cctv")){
                    myJSON_cctv = result;
                    ranking(myJSON_cctv, "response_cctv", ranking_cctv);
                    final_ranking();
                }else if(type.equals("cart")){
                    myJSON = result;
                }
                else if(type.equals("rank")){
                    myJSON_rank = result;
                    rnk_the_list();
                }

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void final_ranking(){
        int i=1;
        for (Map.Entry<Integer, Integer> entry : ranking_police.entrySet()) {
            //ranking map 초기화
            ranking.put(entry.getKey(), i);
            i++;
        }
        i=1;
        for (Map.Entry<Integer, Integer> entry : ranking_delivery.entrySet()) {
            //ranking map 초기화
            int temp=ranking.get(entry.getKey());
            ranking.put(entry.getKey(), temp+i);
            i++;
        }

        i=1;
        for (Map.Entry<Integer, Integer> entry : ranking_safehouse.entrySet()) {
            //ranking map 초기화
            int temp=ranking.get(entry.getKey());
            ranking.put(entry.getKey(), temp+i);
            i++;
        }

        i=1;
        for (Map.Entry<Integer, Integer> entry : ranking_cctv.entrySet()) {
            //ranking map 초기화
            int temp=ranking.get(entry.getKey());
            ranking.put(entry.getKey(), temp+i);
            i++;
        }

        //관심 매물과 ranking에 있는 매물이 일치하지 않을 경우
        //ranking에 없는 관심매물을 ranking의 제일 아래에 넣는다.
        //즉, 안전시설4개가 아예 없는 관심매물을 찾아서 ranking에 넣는다.

        //ranking 정렬
        ranking=sorting(1, ranking);

        //ㅌㅔ스팅
        System.out.println("!!!!!!!##===rank===##!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (Map.Entry<Integer, Integer> entry : ranking.entrySet()) {
            /*System.out.println("key : "+entry.getKey()+"  ,  "+"value : "+entry.getValue());*/
            houserankList.clear();
            loadJSON("http://118.67.131.208/compare_c.php?HID=" + entry.getKey(), "rank");
        }

    }

    public void compareCart(LinkedHashMap<Integer, Integer>m){
        try{
            JSONObject jsonObj = new JSONObject(myJSON);
            response_cartList=jsonObj.getJSONArray("response");

            if(response_cartList.length()!=m.size()) {
                //ranking에 없는 관심 매물 찾기
                for (int i = 0; i < response_cartList.length(); i++) {
                    JSONObject c = response_cartList.getJSONObject(i);
                    atclNo = c.getInt("atclNo");

                    //관심매물이 ranking에 포함되어 있지 않다면 temp에 넣는다.
                    if (!m.containsKey(atclNo)) {
                        prc = c.getInt("prc");
                        rentPrc = c.getInt("rentPrc");
                        prc = prc + rentPrc;
                        temp.put(atclNo, prc);
                    }
                }

                //temp에 있는 매물들을 prc가 적은 순대로 넣는다.
                temp=sorting(1, temp);

                //없는 관심 매물을 ranking에 넣는다.
                System.out.println("=== cartlist ===");
                for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
                    System.out.println("key : " + entry.getKey() + "  ,  " + "value : 0");

                    m.put(entry.getKey(), 0);



                }

                temp.clear();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public LinkedHashMap<Integer, Integer> sorting(int i, Map<Integer, Integer>m){
        ArrayList<Integer> keySetList = new ArrayList<>(m.keySet());
        LinkedHashMap<Integer, Integer> resultMap= new LinkedHashMap<>();
        //내림차순 정렬
        if(i==0){
            // 내림차순 //
            Collections.sort(keySetList, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return m.get(o2).compareTo(m.get(o1));
                }
            });
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
                    Compare_s.this, houserankList, R.layout.list_compare,
                    new String[]{"num","tradTpNm","hanPrc","rentPrc","rletTpNm","atclNm","flrInfo","spc1","atclFetrDesc"},
                    new int[]{R.id.num,R.id.deal_type, R.id.deposit, R.id.monthly_rent, R.id.building_type, R.id.building_name, R.id.floor, R.id.space, R.id.detail_info})
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);
                    LinearLayout linear = (LinearLayout)view.findViewById(R.id.linear);
                    TextView num = (TextView)view.findViewById(R.id.num);
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