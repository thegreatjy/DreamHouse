package org.techtown.project_savedreamhouse;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

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
import android.app.Dialog;
import android.widget.Toast;

//매물 상세 페이지
public class SaleDetail extends AppCompatActivity {
    public static String houseid;
    FragmentPagerAdapter adapterViewPager;

    String myJSON;
    String myJSON_recent;

    Dialog dialog;



    JSONArray estate = null;
    private static final String TAG_RESULTS = "result";

    private static final String TAG_tradtpnm = "tradTpNm";  //전월세
    private static final String TAG_atclnm = "atclNm";      //건물이름
    private static final String TAG_atclcfmymd = "atclCfmYmd";  //등록일자
    private static final String TAG_hanprc = "hanPrc";      //보증금
    private static final String TAG_rentprc = "rentPrc";            //월세
    //private static final String TAG_expense = "expense";        //관리비
    private static final String TAG_rlettpnm = "rletTpNm";      //방 종류
    private static final String TAG_flrinfo = "flrInfo";        //층수
    private static final String TAG_spc1 = "spc1";              //제공 평수
    private static final String TAG_spc2 = "spc2";             //실평수
    private static final String TAG_direction = "direction";    //방향
    private static final String TAG_atclfetrdesc="atclFetrDesc";    //상세 설명
    private static final String TAG_rltrnm="rltrNm";        //중개업자 정보
    private static final String TAG_IMAGE = "repImgUrl";    //이미지 정보
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG= "lng";

    private static TextView tradtpnm;
    private static TextView atclnm;
    private static TextView atclcfmymd;

    private static TextView prc;
    private static TextView expense;
    private static TextView rlettpnm;
    private static TextView flrinfo;
    private static TextView spc;
    private static TextView direction;
    private static TextView atclfetrdesc;
    private static TextView rltrnm;
    private static WebView wv;

    Button button_call;
    Button toMap;
    Button button_report;
    String LAT;
    String LNG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        houseid = getIntent().getStringExtra("houseID");
        addTemp();

        wv = (WebView)findViewById(R.id.wv);

        tradtpnm = (TextView)findViewById(R.id.rent);
        atclnm = (TextView)findViewById(R.id.building_name);
        atclcfmymd = (TextView)findViewById(R.id.date);

        prc = (TextView)findViewById(R.id.rent_price);
        expense = (TextView)findViewById(R.id.maintenance_cost);
        rlettpnm = (TextView)findViewById(R.id.room_type);
        flrinfo = (TextView)findViewById(R.id.floor);
        spc = (TextView)findViewById(R.id.area);
        direction = (TextView)findViewById(R.id.direction);

        atclfetrdesc = (TextView)findViewById(R.id.textView_detail_info);

        rltrnm = (TextView)findViewById(R.id.textView_office_name);

        button_call = (Button) findViewById(R.id.button_call);
        toMap = (Button)findViewById(R.id.toMap);
        button_report = (Button) findViewById(R.id.button_report);

        getData_list("http://118.67.131.208/PHP_saleDetail.php", houseid);

        /*Toast toast = Toast.makeText(this,A,Toast.LENGTH_LONG);
        toast.show();*/


        WebView wv=findViewById(R.id.wv);

        WebSettings settings=wv.getSettings();
        settings.setJavaScriptEnabled(true);

        wv.setWebViewClient(new WebViewClient());
        //2. alert(), comfirm() 같은 팝업기능의 JS코드가 사용가능하도록하는 코드 필요
        wv.setWebChromeClient(new WebChromeClient());

        dialog = new Dialog(SaleDetail.this);       // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setContentView(R.layout.dialog);             // xml 레이아웃 파일과 연결

        //전화걸기 버튼 누르면 연결
        button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:01012345678"));
                startActivity(intent);
            }
        });

        //신고 버튼
        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "신고되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        //지도로보기 버튼 누르면 연결
        toMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleDetail.this, DetailToMap.class);
                intent.putExtra("houseId", houseid);
                //위도 경도 전달
                intent.putExtra("lat", Double.parseDouble(LAT));
                intent.putExtra("lng", Double.parseDouble(LNG));
                startActivity(intent);
            }
        });

    }



    public void getData_list(String url,String search) {

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

                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    estate = jsonObj.getJSONArray(TAG_RESULTS);

                    JSONObject c = estate.getJSONObject(0);
                    String AA= "https://landthumb-phinf.pstatic.net/"+c.getString(TAG_IMAGE);
                    String A = c.getString(TAG_tradtpnm);
                    String B = c.getString(TAG_atclnm);
                    String C = c.getString(TAG_atclcfmymd);
                    String D = c.getString(TAG_hanprc)+" / "+c.getString(TAG_rentprc);
                    String E = "15";   //c.getString(TAG_expense);
                    String F = c.getString(TAG_rlettpnm);
                    String G = c.getString(TAG_flrinfo);
                    String H = c.getString(TAG_spc1)+" / "+c.getString(TAG_spc2);
                    String I = c.getString(TAG_direction);
                    String J = c.getString(TAG_atclfetrdesc);
                    String K = c.getString(TAG_rltrnm);
                    LAT = c.getString(TAG_LAT);
                    LNG = c.getString(TAG_LNG);

                    //wv.loadUrl(AA);
                    imgcng(AA);

                    tradtpnm.setText(A);
                    atclnm.setText(B);
                    atclcfmymd.setText(C);
                    prc.setText(D);
                    expense.setText(E);
                    rlettpnm.setText(F);
                    flrinfo.setText(G);
                    spc.setText(H);
                    direction.setText(I);
                    atclfetrdesc.setText(J);
                    rltrnm.setText(K);

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
    public  String creHtmlBody(String imagUrl){
        StringBuffer sb = new StringBuffer("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0; padding:0; text-align:center;'>");    //중앙정렬
        sb.append("<img src=\"" + imagUrl+"\">");    //지 비율에 맞게 나옴

        sb.append("<img width='100%' height='100%' src=\"" + imagUrl+"\">"); //가득차게 나옴

        sb.append("</BODY>");
        sb.append("</HTML>");

        return sb.toString();
    }

    public void imgcng(String url){

        wv.setVerticalScrollBarEnabled(false);
        wv.setVerticalScrollbarOverlay(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setHorizontalScrollbarOverlay(false);
        wv.setInitialScale(100);
        wv.loadDataWithBaseURL(null,creHtmlBody(url), "text/html", "utf-8", null);

    }

    public void addTemp(){
        try {
            loadJSON("http://118.67.131.208/recentEstates_insert.php?userID=" + URLEncoder.encode(MainActivity.userID, "UTF-8")+"&&houseID="+houseid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

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
                myJSON_recent = result;
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    // dialog01을 디자인하는 함수
    public void showDialog(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 1000;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show(); // 다이얼로그 띄우기
        Window window = dialog.getWindow();
        window.setAttributes(lp);

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 닫기 버튼
        Button button_ok = dialog.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // 다이얼로그 닫기
            }
        });

    }
}