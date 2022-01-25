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

import androidx.appcompat.app.AppCompatActivity;

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

//관심 매물
public class Cart extends AppCompatActivity {

    String myJSON_cart;
    String myJSON_delete;
    JSONArray carts = null;

    ArrayList<HashMap<String, String>> cartList;
    ListAdapter adapter;
    private List<House> houseList;
    ListView cartlist;
    Button button_safety;
    Button button_convenience;
    private static final String response = "response";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartList = new ArrayList<HashMap<String, String>>();
        cartlist= (ListView)findViewById(R.id.listView_cart);
        houseList = new ArrayList<House>();
        try {
            cartList.clear();
            myCart("http://118.67.131.208/CartList.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8"),"cart");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //안전순(경찰서)
        //select문으로 600m안에 있는 경찰서 개수 가져오기
        button_safety = (Button) findViewById(R.id.compare_safety);
        button_convenience = (Button) findViewById(R.id.compare_convenience);
        //안전순(택배함)

        //안전 순위로 나열하는 화면으로 이동
        button_safety.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Compare_s.class);
                startActivity(intent);
            }
        });
        //편의 순위로 나열하는 화면으로 이동
        button_convenience.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Compare_c.class);
                startActivity(intent);
            }
        });
    }
    protected void upLoad(){
        try{
            JSONObject jsonObj=new JSONObject(myJSON_cart);
            carts=jsonObj.getJSONArray(response);

            int count=0;

            String houseID;
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

            while(count<carts.length()){
                JSONObject object=carts.getJSONObject(count);
                houseID = object.getString("atclNo");
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

                HashMap<String, String> houseCarts = new HashMap<String, String>();
                houseCarts.put("search_area",houseGudong);
                houseCarts.put("tradTpNm",houseSort);
                houseCarts.put("hanPrc",housePrice);
                houseCarts.put("rentPrc",houseRent);
                houseCarts.put("rletTpNm",houseRoom);
                houseCarts.put("atclNm",houseAtcl);
                houseCarts.put("flrInfo",houseFloor);
                houseCarts.put("spc1",houseSpace1);
                houseCarts.put("spc2",houseSpace2);
                houseCarts.put("atclFetrDesc",houseDetail);

                House house = new House(houseID);
                houseList.add(house);
                cartList.add(houseCarts);
                count++;
            }
            adapter = new SimpleAdapter(
                    Cart.this, cartList, R.layout.list_cart,
                    new String[]{"tradTpNm","hanPrc","rentPrc","rletTpNm","atclNm","flrInfo","spc1", "spc2","atclFetrDesc"},
                    new int[]{R.id.deal_type, R.id.deposit, R.id.monthly_rent, R.id.building_type, R.id.building_name, R.id.floor, R.id.space, R.id.space2, R.id.detail_info})
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);

                    Button button_checklist = (Button)view.findViewById(R.id.button_checklist);
                    Button button_cancel = (Button)view.findViewById(R.id.button_cancel);

                    LinearLayout connectcart = (LinearLayout)view.findViewById(R.id.cartconnect);
                    connectcart.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            Intent intent = new Intent(getApplicationContext(), SaleDetail.class);
                            intent.putExtra("houseID", houseList.get(position).getHouseID());
                            startActivity(intent);
                        }
                    });
                    button_checklist.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), CheckList.class);
                            intent.putExtra("houseID", houseList.get(position).getHouseID());
                            startActivity(intent);
                        }
                    });
                    //관심 매물 삭제
                    button_cancel.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {

                            try {
                                myCart("http://118.67.131.208/deleteCart.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8")+"&houseID="+URLEncoder.encode(houseList.get(position).getHouseID(), "UTF-8"), "delete");
                                startActivity(getIntent());
                                finish();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return view;
                }
            };
            cartlist.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void myCart(String url, String JsonType) {
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
                if(JsonType.equals("cart")){
                    myJSON_cart = result;
                    upLoad();
                }else if(JsonType.equals("delete")){
                    myJSON_delete = result;
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}