package org.techtown.project_savedreamhouse;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static String userID;//모든 클래스에서 접근이 가능하도록

    //알람 변수
    long mNow;
    Date mDate;
    public static int n2;
    public static String atclno2;

    SimpleDateFormat mFormat = new SimpleDateFormat("yy.MM.dd");

    public String packageName = "com.DefaultCompany.argps"; // 유니티 AR 앱

    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    private Context activity;

    boolean NOTI;

    SharedPreferences pref;          // 프리퍼런스
    SharedPreferences.Editor editor; // 에디터


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        userID = getIntent().getStringExtra("id");

        TextView user_id = (TextView)findViewById(R.id.user_id);
        user_id.setText(userID);

        Button button_noti = (Button)findViewById(R.id.button_noti);
        button_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotiFilter.class);
                startActivity(intent);
            }
        });

        //알림 기능
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mCalender = new GregorianCalendar();

        // 1. Shared Preference 초기화
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        // 2. 저장해둔 값 불러오기 ("식별값", 초기값) -> 식별값과 초기값은 직접 원하는 이름과 값으로 작성.
        NOTI = pref.getBoolean("tf", false); //Boolean 불러오기 (저장해둔 값 없으면 초기값 false로 불러옴)

        if(NOTI){
            setAlarm();
        }


        LinearLayout button_latest = (LinearLayout)findViewById(R.id.button_latest);
        button_latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LatestSale.class);
                startActivity(intent);
            }
        });

        //(1) 매물 찾기
        //Button Button1 = (Button) findViewById(R.id.button_find_sale);
        LinearLayout findsale = (LinearLayout)findViewById(R.id.findsale);
        findsale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FindSale.class);
                startActivity(intent);
            }
        });

        //(2) 관심 매물
        //Button Button2 = (Button) findViewById(R.id.button_watchlist);
        LinearLayout watchlist = (LinearLayout) findViewById(R.id.watchlist);
        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Cart.class);
                startActivity(intent);
            }
        });
        //(3)
        //Intent intent = this.getPackageManager().getLaunchIntentForPackage(packageName);

        LinearLayout community = (LinearLayout) findViewById(R.id.community);
        //Button Button3 = (Button) findViewById(R.id.button_ar);
        LinearLayout ar = (LinearLayout) findViewById(R.id.ar);
        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AR.class);
                startActivity(intent);
            }
        });
        //(4) + 더보기
        //Button button_plus = (Button) findViewById(R.id.button_plus);

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Community.class);
                startActivity(intent);

            }
        });

        //최근 본 방

    }

    //RoutineAlarm.java 여기에 잠들다...
    private void setAlarm() {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, receiverIntent, 0);
        //String from = "2021-05-16 08:10:30"; //임의로 날짜와 시간을 지정

        getData_user("http://118.67.131.208/getUserFilter.php",userID);

        //Toast.makeText(this, n, Toast.LENGTH_SHORT).show();

        //알림 울릴 매물이 1개 이상일 경우 울림

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 3); //시
        calendar.set(Calendar.MINUTE, 3);   //분
        //위의 시간에 알림 울림!
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);


    }

    private String getDate() {      //오늘 등록된 매물만 알림에 뜨도록 하는데 필요.
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    public void getData_user(String url,String id) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;

                String search_query = "userID="+id;
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

                String myJSON;
                JSONArray userFilter = null;
                final String TAG_RESULTS = "result";
                final String TAG_userid = "user_id";
                final String TAG_area = "search_area";      //알림 설정된 지역

                myJSON = result;

                String A;
                String B;

                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    userFilter = jsonObj.getJSONArray(TAG_RESULTS);
                    //for (int i = 0; i < userFilter.length(); i++) {       //일단 사용자 id 하나로...

                    JSONObject c = userFilter.getJSONObject(0);
                    A = c.getString(TAG_userid);
                    B = c.getString(TAG_area);


                    getData_alarm("http://118.67.131.208/getAlarm.php", B,getDate());
                    //System.out.println("n="+n);
                    //php 파일 만들어야 함.


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

    public void getData_alarm(String url, String area, String date) {


        class GetDataJSON2 extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String search_query = "area=" + area + "&today=" + date;
                Map<String,String> map;

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

                    /*OutputStream outputstrim2 = con.getOutputStream();
                    outputstrim2.write(search_query2.getBytes("UTF-8"));
                    outputstrim2.flush();
                    outputstrim2.close();*/


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

                String myJSON2;
                JSONArray alarm = null;
                final String TAG_RESULTS2 = "result";
                final String TAG_TEMP = "atclNo";        //임시용

                myJSON2 = result;
                String a = null;

                int m=0;
                try {
                    JSONObject jsonObj2 = new JSONObject(myJSON2);
                    alarm = jsonObj2.getJSONArray(TAG_RESULTS2);

                    for (int i = 0; i < alarm.length(); i++) {

                        JSONObject c = alarm.getJSONObject(i);

                        a = c.getString(TAG_TEMP);
                        m=m+1;       //일단 개수만 알려주게 한다.
                    }
                    n2=m;
                    atclno2 = a;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON2 h = new GetDataJSON2();
        h.execute(url);
        //return m;       //여기가 먼저 발생하고 m==이 나중에 발생함.....
    }

    //헤드업



}