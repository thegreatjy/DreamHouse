package org.techtown.project_savedreamhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

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
import java.util.HashMap;
import java.util.Map;

//매물 상세 페이지에서 주변 시설 확인
public class DetailToMap extends AppCompatActivity implements OnMapReadyCallback {
    public static String houseid;

    private MapView mapView;
    private static NaverMap naverMap;
    //1:안전순 2: 편의순
    int cur_pin=0;
    int curr_pin=0;

    double LATITUDE;
    double LONGITUDE;

    private static final String Results = "result";

    private static final String Lat = "latitude";
    private static final String Lng = "longitude";

    JSONArray results = null;
    JSONArray re = null;
    JSONArray cctvs = null;
    JSONArray safes = null;
    JSONArray boxes = null;

    String myJSON;

    //위경도
    String a;
    String b;
    double aa;
    double bb;

    Marker marker ;
    ArrayList<Marker> markerArray = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_map);

        houseid = getIntent().getStringExtra("houseId");
        //Toast.makeText(DetailToMap.this,houseid , Toast.LENGTH_LONG).show();

        LinearLayout ClickedSafety = (LinearLayout)findViewById(R.id.ClickedSafety);
        LinearLayout ClickedConvenience = (LinearLayout)findViewById(R.id.ClickedConvenience);

        Button button_safety = (Button) findViewById(R.id.button_safety);
        Button button_facility = (Button) findViewById(R.id.button_facility);
        Button button_justclick = (Button)findViewById(R.id.justclick);

        Button button_crime2 = (Button)findViewById(R.id.button_crime2);

        Button near_police = findViewById(R.id.near_police);
        Button near_cctv=findViewById(R.id.near_cctv);
        Button near_safe = findViewById(R.id.near_safe);
        Button near_box=findViewById(R.id.near_box);

        Button near_convenience=findViewById(R.id.near_convenience);
        Button near_pharmacy = findViewById(R.id.near_pharmacy);
        Button near_hospital = findViewById(R.id.near_hospital);
        Button near_subway=findViewById(R.id.near_subway);
        Button near_busstop = findViewById(R.id. near_busstop);

        LATITUDE = getIntent().getDoubleExtra("lat",0);
        LONGITUDE = getIntent().getDoubleExtra("lng",0);
        System.out.println(LATITUDE+" "+LONGITUDE);

        getData("http://118.67.131.208/nearEstate.php?houseID=" + houseid);

        mapView = (MapView) findViewById(R.id.map_view2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);

        //intent 켜지자 마자 바로 버튼 눌리도록
        button_justclick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setMarker(new Marker(), LATITUDE, LONGITUDE, R.drawable.ic_baseline_place_24_purple, 1);
            }
        });
        button_justclick.performClick();

        button_safety.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //setMarker(new Marker(), LAT, LNG, R.drawable.ic_baseline_place_24_purple, 1);
                if(cur_pin!=1){
                    ClickedSafety.setVisibility(View.VISIBLE);
                    isOnMap(1);
                }else{
                    ClickedSafety.setVisibility(View.GONE);
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }
            }
        });
        button_facility.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cur_pin!=2){
                    ClickedConvenience.setVisibility(View.VISIBLE);
                    isOnMap(2);
                }else{
                    ClickedConvenience.setVisibility(View.GONE);
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }
            }
        });
        near_police.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=1){
                    isOnMap(1);
                    resetMarker();
                    try {
                        ListView(myJSON,0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_cctv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=2){
                    isOnMap(2);
                    resetMarker();
                    try {
                        ListView(myJSON,1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_safe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=3){
                    isOnMap(3);
                    resetMarker();
                    try {
                        ListView(myJSON,2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_box.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=4){
                    isOnMap(4);
                    resetMarker();
                    try {
                        ListView(myJSON,3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });

        near_convenience.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=5){
                    isOnMap(5);
                    resetMarker();
                    try {
                        ListView(myJSON,4);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_pharmacy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=6){
                    isOnMap(6);
                    resetMarker();
                    try {
                        ListView(myJSON,5);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_hospital.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=7){
                    isOnMap(7);
                    resetMarker();
                    try {
                        ListView(myJSON,6);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_subway.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=8){
                    isOnMap(8);
                    resetMarker();
                    try {
                        ListView(myJSON,7);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });
        near_busstop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(curr_pin!=9){
                    isOnMap(9);
                    resetMarker();
                    try {
                        ListView(myJSON,8);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //안전필터 버튼 누르면 아예 다 사라짐.
                    resetMarker();         //마커 초기화
                    iisOnMap(0);
                }
            }
        });

        button_crime2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), crimeHTML.class);
                startActivity(intent);
            }
        });


    }

    //it == 현재 지도에 표시된 pin 종류
    //2==cctv, 3==안심택배함, 4==안심 지킴이집, 5==지구대
    public void isOnMap(int it){
        //현재 지도에 표시된 pin 종류가 표시하려는 종류와 같을 경우 => 지운다.
        if(cur_pin==it){
            cur_pin=0;
            curr_pin=0;
        }else{  //다른 종류의 pin을 표시할 경우 => 바꾼다.
            cur_pin=it;
        }
    }
    public void iisOnMap(int it){
        //현재 지도에 표시된 pin 종류가 표시하려는 종류와 같을 경우 => 지운다.
        if(curr_pin==it){
            curr_pin=0;
        }else{  //다른 종류의 pin을 표시할 경우 => 바꾼다.
            curr_pin=it;
        }
    }

    //마커 모두 지우기
    private void resetMarker(){
        for(int i=0;i<markerArray.size();i++){
            markerArray.get(i).setMap(null);
        }
    }

    //마커 위도 경도 설정
    private void setMarker(Marker marker, double lat, double lng, int resourceID, int zIndex)
    {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);
        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(resourceID));
        //마커의 투명도
        marker.setAlpha(0.8f);
        //마커 위치
        marker.setPosition(new LatLng(lat, lng));
        //마커 우선순위
        marker.setZIndex(zIndex);
        //마커 표시
        marker.setMap(naverMap);

        marker.setWidth(70);
        marker.setHeight(70);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap)
    {
        this.naverMap = naverMap;

        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Navi);

        //건물 표시
        naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);

        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(LATITUDE, LONGITUDE),   // 위치 지정
                16,                                     // 줌 레벨
                30,                                       // 기울임 각도
                0                                     // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //url.php에서 데이터 가져오는 함수
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
                myJSON=result;

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
    private void ListView(String myJSON,int num) throws JSONException {
        JSONObject jsonObj = new JSONObject(myJSON);
        results = jsonObj.getJSONArray("result");

        resetMarker();
        JSONObject p = results.getJSONObject(num);

        int icon = R.drawable.ic_baseline_place_24_blue;
        switch(num){
            case 0:
                re=p.getJSONArray("police");
                icon = R.drawable.ic_baseline_near_police_24;
                break;
            case 1:
                re=p.getJSONArray("cctv");
                icon = R.drawable.ic_baseline_near_camera_24;
                break;
            case 2:
                re=p.getJSONArray("safehouse");
                icon=R.drawable.ic_baseline_near_safe_24;
                break;
            case 3 :
                re=p.getJSONArray("delivery");
                icon = R.drawable.ic_baseline_near_box_24;
                break;
            case 4 :
                re=p.getJSONArray("convenience");
                icon=R.drawable.ic_baseline_store_24;
                break;
            case 5 :
                re=p.getJSONArray("pharmacy");
                icon = R.drawable.ic_baseline_near_pharmacy_24;
                break;
            case 6 :
                re=p.getJSONArray("hospital");
                icon=R.drawable.ic_baseline_near_hospital_24;
                break;
            case 7 :
                re=p.getJSONArray("subway");
                icon = R.drawable.ic_baseline_near_subway_24;
                break;
            case 8 :
                re=p.getJSONArray("busstop");
                icon=R.drawable.ic_baseline_near_bus_24;
                break;
            default :
                System.out.println("그 외의 숫자");
        }

        for(int i=0;i<re.length();i++){
            marker = new Marker();
            markerArray.add(marker);

            JSONObject c = re.getJSONObject(i);
            a = c.getString(Lat);
            b = c.getString(Lng);
            aa = Double.parseDouble(a);
            bb = Double.parseDouble(b);
            setMarker(markerArray.get(i), aa, bb, icon, 1);
            System.out.println("a:"+a+" "+"b:"+b);
        }

    }

}