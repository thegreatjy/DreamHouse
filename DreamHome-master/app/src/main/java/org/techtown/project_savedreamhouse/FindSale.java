package org.techtown.project_savedreamhouse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//홈 화면->매물 찾기 버튼 눌렀을 때 화면
//즉, 지도랑 안전 시설 필터를 확인 + 상단에는 매물 검색할 수 있는 화면
//매물 검색은 FindSaleList
public class FindSale extends AppCompatActivity implements OnMapReadyCallback {
    public static String HOUSEID;

    private MapView mapView;
    private static NaverMap naverMap;


    private static final String Results = "result";

    private static final String Lat = "latitude";
    private static final String Lng = "longitude";
    private static final String Lat2 = "latitude";
    private static final String Lng2 = "longitude";
    private static final String Lat3 = "latitude";
    private static final String Lng3 = "longitude";
    private static final String Lat4 = "latitude";
    private static final String Lng4 = "longitude";

    JSONArray cctvs = null;
    JSONArray polices = null;
    JSONArray delivery = null;
    JSONArray safehouse = null;

    String myJSON_cctv;
    String myJSON_police;
    String myJSON_delivery;
    String myJSON_safehouse;

    String myJSON_list;

    //cctv
    String s;
    String t;
    double ss;
    double tt;
    //police
    String p;
    String q;
    double pp;
    double qq;
    //delivery
    String a;
    String b;
    double aa;
    double bb;
    //safehouse
    String d;
    String f;
    double dd;
    double ff;

    //수민
    //mainActivity에서 userID가져오기
    String userID = MainActivity.userID;
    //매물 찜하기 버튼
    private Button button_jjim;
    boolean isClick_jjim;


    //현재 지도에 표시된 pin 종류
    //1==범죄발생수, 2==cctv, 3==안심택배함, 4==안심 지킴이집, 5==지구대
    private int current_pin=0;

    //매물 마커 클릭 시 사용하는 변수.
    private InfoWindow infoWindow = new InfoWindow();

    // FindSaleList 변수****

    Bitmap bitmap;
    private static final String TAG_RESULTS = "result";
    private static final String houseNO = "atclNo";
    private static final String TAG_SEARCH_AREA = "search_area";
    private static final String TAG_TRADTPNM = "tradTpNm";
    private static final String TAG_HANPRC = "hanPrc";
    private static final String TAG_RENTPRC = "rentPrc";
    private static final String TAG_RLETTPNM = "rletTpNm";
    private static final String TAG_ATCLNM = "atclNm";
    private static final String TAG_FLRINFO = "flrInfo";
    private static final String TAG_SPC1 = "spc1";
    private static final String TAG_SPC2 = "spc2";
    private static final String TAG_ATCLFETRDESC = "atclFetrDesc";
    private static final String TAG_IMAGE = "repImgUrl";    //이미지 정보
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG= "lng";

    private ImageView realImage;


    JSONArray peoples = null;

    ArrayList<HashMap<String, String>> personList;
    //houseID가져올 houseList
    private List<House> houseList;

    ListView list;

    private EditText search_text;
    private Button button_search;
    private Button button_change;
    private int changeInt=0;

    Marker marker ;
    ArrayList <Marker> markerArray = new ArrayList<Marker>();
    WebView webView;        //몰러....이미지 난 몰러....
    ArrayList <WebView> wvArray = new ArrayList<>();

    //위치 바꾸기
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_sale);
        LinearLayout fragment_contain = (LinearLayout) findViewById(R.id.fragment_container);
        ScrollView scrollView= (ScrollView) findViewById(R.id.scrollView);

        //하단의 <범죄 발생률/cctv/안심택배함/안심 지킴이집/지구대> 다섯 개의 버튼
        Button button_crime = (Button) findViewById(R.id.button_crime);
        Button button_cctv = (Button) findViewById(R.id.button_cctv);
        Button button_police = (Button) findViewById(R.id.button_police);
        Button button_delivery_box = (Button) findViewById(R.id.button_delivery_box);
        Button button_safe_keeper = (Button) findViewById(R.id.button_safe_keeper);

        //성범죄자 알림e 링크 연결 버튼
        Button sexoffender = (Button) findViewById(R.id.sexoffender);

        //상단의 매물 검색란, 검색 버튼
        search_text=(EditText) findViewById(R.id.search_tx);
        button_search = (Button) findViewById(R.id.button_search1);
        button_change = (Button)findViewById(R.id.button_change);


        //php에서 데이터 가지고 오기
        getData("http://118.67.131.208/cctv.php", "cctv");
        getData("http://118.67.131.208/police.php", "police");
        getData("http://118.67.131.208/delivery.php", "delivery");
        getData("http://118.67.131.208/safehouse.php", "safehouse");

        //FindSaleList****
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String, String>>();
        //houseID저장할 arrayList
        houseList=new ArrayList<House>();

        isClick_jjim = false;

        // FindSale, 매물 검색 버튼 누르면 실행
        button_search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                personList.clear();
                houseList.clear();

                getData_list("http://118.67.131.208/PHP_connection.php", search_text.getText().toString());
            }
        });

        // FindSale, 매물 검색 버튼 누르면 실행
        button_change.setOnClickListener(new View.OnClickListener(){     //검색 버튼 누르면 레이아웃 없앰.
            @Override
            public void onClick(View v) {

                if(changeInt==0){
                    fragment_contain.setVisibility(View.GONE);
                    scrollView.setVisibility((View.VISIBLE));

                    //FindSaleList****
                    personList.clear();
                    houseList.clear();

                    getData_list("http://118.67.131.208/PHP_connection.php", search_text.getText().toString());


                    changeInt++;

                }else {
                    fragment_contain.setVisibility(View.VISIBLE);
                    scrollView.setVisibility((View.GONE));

                    personList.clear();
                    houseList.clear();

                    getData_list("http://118.67.131.208/PHP_connection.php", search_text.getText().toString());


                    changeInt--;
                }
            }
        });

        //성범죄자 알림e 버튼 누르면 실행
        sexoffender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SexOffenderHTML.class);
                startActivity(intent);
            }
        });

        //(1) 범죄 발생수 버튼 누르면 실행
        button_crime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), crimeHTML.class);
                startActivity(intent);
            }
        });

        //(2) cctv 버튼 누르면 실행
        button_cctv.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //cctv버튼이 안눌려진 상태 : cctv 핀을 생성한다.
                if(current_pin!=2) {
                    try {
                        isOnMap(2);
                        resetMarker();

                        JSONObject jsonObj = new JSONObject(myJSON_cctv);
                        cctvs = jsonObj.getJSONArray(Results);

                        for (int i = 0; i < cctvs.length(); i++) {
                            marker = new Marker();
                            markerArray.add(marker);


                            JSONObject c = cctvs.getJSONObject(i);
                            s = c.getString(Lat);
                            t = c.getString(Lng);
                            ss = Double.parseDouble(s);
                            tt = Double.parseDouble(t);
                            setMarker(markerArray.get(i), ss, tt, R.drawable.ic_baseline_place_24_red, 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(FindSale.this, Boolean.toString(isClick_cctv), Toast.LENGTH_SHORT).show();

                } else{
                    //cctv 버튼이 눌려져 있는 상태 : cctv 핀을 삭제해준다.
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }

            }
        });

        //(3) 안심택배함 누르면 실행
        button_delivery_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_pin!=3) {
                    try {
                        isOnMap(3);
                        resetMarker();

                        JSONObject jsonObj = new JSONObject(myJSON_delivery);
                        delivery = jsonObj.getJSONArray(Results);

                        for (int i = 0; i < delivery.length(); i++) {
                            marker = new Marker();
                            markerArray.add(marker);


                            JSONObject c = delivery.getJSONObject(i);
                            a = c.getString(Lat3);
                            b = c.getString(Lng3);
                            aa = Double.parseDouble(a);
                            bb = Double.parseDouble(b);
                            setMarker(markerArray.get(i), aa, bb, R.drawable.ic_baseline_place_24_green, 1);
                            /*marker.setOnClickListener(new Overlay.OnClickListener() {
                                @Override
                                public boolean onClick(@NonNull Overlay overlay) {
                                    Toast.makeText(getApplication(), "마커1 클릭", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });*/
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(FindSale.this, Boolean.toString(isClick_cctv), Toast.LENGTH_SHORT).show();

                }else{
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }
            }
        });


        //(4) 안심 지킴이집 누르면 실행
        button_safe_keeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_pin!=4) {
                    try {
                        isOnMap(4);
                        resetMarker();

                        JSONObject jsonObj = new JSONObject(myJSON_safehouse);
                        safehouse = jsonObj.getJSONArray(Results);

                        for (int i = 0; i < safehouse.length(); i++) {
                            marker = new Marker();
                            markerArray.add(marker);


                            JSONObject c = safehouse.getJSONObject(i);
                            d = c.getString(Lat4);
                            f = c.getString(Lng4);
                            dd = Double.parseDouble(d);
                            ff = Double.parseDouble(f);
                            setMarker(markerArray.get(i), dd, ff, R.drawable.ic_baseline_place_24_yellow, 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }
            }
        });


        //(5) 지구대 버튼 누르면 실행
        button_police.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(current_pin!=5){
                    try{
                        isOnMap(5);
                        resetMarker();

                        JSONObject jsonObj = new JSONObject(myJSON_police);
                        polices=jsonObj.getJSONArray(Results);

                        for(int i=0; i<polices.length(); i++) {
                            marker = new Marker();
                            markerArray.add(marker);

                            JSONObject c = polices.getJSONObject(i);
                            p = c.getString(Lat2);
                            q = c.getString(Lng2);
                            pp = Double.parseDouble(p);
                            qq = Double.parseDouble(q);
                            setMarker(markerArray.get(i), pp, qq, R.drawable.ic_baseline_place_24_blue, 1);
                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    resetMarker();         //마커 초기화
                    isOnMap(0);
                }

            }
        });

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);

    }

    //url.php에서 데이터 가져오는 함수
    public void getData(String url, String JsonType) {
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
                //Json Type에 따라 각자 다른 json에 result 저장
                if(JsonType.equals("cctv")){
                    myJSON_cctv = result;
                }else if(JsonType.equals("police")){
                    myJSON_police = result;
                }else if(JsonType.equals("crimes")){

                }else if(JsonType.equals("delivery")){
                    myJSON_delivery = result;
                }else if(JsonType.equals("safehouse")){
                    myJSON_safehouse = result;
                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    //it == 현재 지도에 표시된 pin 종류
    //2==cctv, 3==안심택배함, 4==안심 지킴이집, 5==지구대
    public void isOnMap(int it){
        //현재 지도에 표시된 pin 종류가 표시하려는 종류와 같을 경우 => 지운다.
        if(current_pin==it){
            current_pin=0;
        }else{  //다른 종류의 pin을 표시할 경우 => 바꾼다.
            current_pin=it;
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

        marker.setWidth(100);
        marker.setHeight(100);
    }
    private void setMarker2(Marker marker, double lat, double lng, int resourceID, int zIndex)
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

        marker.setWidth(50);
        marker.setHeight(50);
    }

    /*
    private void deleteMarker(Marker marker){
        marker.setMap(null);
    }
    */

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
                new LatLng(   37.482104027205, 126.95982056543),   // 위치 지정
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
    //FindSaleList****
    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON_list);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);



            resetMarker();

            for (int i = 0; i < peoples.length(); i++) {
                marker = new Marker();
                markerArray.add(marker);

                //webView=findViewById(R.id.webview);

                JSONObject c = peoples.getJSONObject(i);
                //String A = "매물번호 "+c.getString(TAG_id);        //매물번호

                //realImage = (ImageView) findViewById(R.id.estate_image);

                String AA = c.getString(houseNO);
                String houseID = c.getString(houseNO);

                String A = c.getString(TAG_SEARCH_AREA);

                String B = c.getString(TAG_TRADTPNM)+" ";
                String C = c.getString(TAG_HANPRC);
                String D = "/"+c.getString(TAG_RENTPRC);

                String E = c.getString(TAG_RLETTPNM);
                String F = c.getString(TAG_ATCLNM);
                String FF = " · "+F;

                String G = c.getString(TAG_FLRINFO)+"층 ";
                String H = " "+c.getString(TAG_SPC1)+"/";
                String I = " "+c.getString(TAG_SPC2)+"평";

                String J = c.getString(TAG_ATCLFETRDESC);
                String K = c.getString(TAG_IMAGE);


                String L = c.getString(TAG_LAT);
                String M = c.getString(TAG_LNG);

                pp = Double.parseDouble(L);
                qq = Double.parseDouble(M);
                setMarker2(markerArray.get(i), pp, qq, R.drawable.ic_baseline_home_24, 1);

                //마커 클릭시 이벤트 발생.
                int finalI = i;
                markerArray.get(i).setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        {
                            ViewGroup rootView = (ViewGroup)findViewById(R.id.fragment_container);
                            pointAdapter adapter = new pointAdapter(FindSale.this, rootView,F+"  ("+E+")",B+C+D,A);

                            infoWindow.setAdapter(adapter);

                            //인포창의 우선순위
                            infoWindow.setZIndex(10);
                            //투명도 조정
                            infoWindow.setAlpha(0.9f);
                            //인포창 표시
                            infoWindow.open(markerArray.get(finalI));
                            return false;
                        }
                    }
                });

                HashMap<String, String> persons = new HashMap<String, String>();


                persons.put(houseNO,AA);
                persons.put(TAG_SEARCH_AREA, A);
                persons.put(TAG_TRADTPNM,B);
                persons.put(TAG_HANPRC, C);
                persons.put(TAG_RENTPRC, D);
                persons.put(TAG_RLETTPNM,E);
                persons.put(TAG_ATCLNM,FF);
                persons.put(TAG_FLRINFO,G);
                persons.put(TAG_SPC1,H);
                persons.put(TAG_SPC2,I);
                persons.put(TAG_ATCLFETRDESC,J);


                //Glide.with(this).load(K).into(realImage);
                House house = new House(houseID);
                houseList.add(house);
                personList.add(persons);
            }

            //지도 위치 바꾸기
            latLng=new LatLng(pp,qq);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(latLng)
                    .animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);

            ListAdapter adapter = new SimpleAdapter(
                    FindSale.this, personList, R.layout.list_item,
                    new String[]{TAG_SEARCH_AREA, TAG_TRADTPNM,TAG_HANPRC,TAG_RENTPRC,TAG_RLETTPNM,TAG_ATCLNM,TAG_FLRINFO,TAG_SPC1,TAG_SPC2,TAG_ATCLFETRDESC},
                    new int[]{R.id.search_area,R.id.tradTpNm,R.id.hanPrc,R.id.comment_writer_id,R.id.rletTpNm,R.id.atclNm,R.id.flrInfo,R.id.spc1,R.id.spc2,R.id.atclFetrDesc})
            {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent){
                    View view = super.getView(position,convertView,parent);
                    Button button_jjim = (Button)view.findViewById(R.id.button_jjim);
                    LinearLayout button_sale = (LinearLayout)view.findViewById(R.id.salebutton);
                    button_jjim.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            /*String userID = MainActivity.userID;*///mainactivitiy의 userID세션 가져오기
                            if(isClick_jjim == false){
                                button_jjim.setBackgroundResource(R.drawable.clicked_heart);
                                isClick_jjim = true;
                            } else{
                                button_jjim.setBackgroundResource(R.drawable.heart);
                                isClick_jjim = false;
                            }
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject( response );
                                        boolean success = jsonObject.getBoolean( "success" );
                                        //
                                        if(success) {
                                            Toast.makeText( getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT ).show();
                                            //회원가입 실패시
                                        } else {
                                            Toast.makeText( getApplicationContext(), "실패", Toast.LENGTH_SHORT ).show();
                                            return;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            AddRequest addRequest = new AddRequest(userID, houseList.get(position).getHouseID(), responseListener);
                            RequestQueue queue = Volley.newRequestQueue(FindSale.this);
                            queue.add(addRequest);
                        }
                    });
                    button_sale.setOnClickListener(new View.OnClickListener(){
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData_list(String url,String search) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String search_query = "search="+search;

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
                myJSON_list = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}