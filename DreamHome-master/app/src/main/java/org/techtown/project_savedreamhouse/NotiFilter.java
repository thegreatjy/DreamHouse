package org.techtown.project_savedreamhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//매물 알림
public class NotiFilter extends AppCompatActivity {

    Dialog dialog;

    String myJSON;
    String user_id;
    String alarmSelect;

    //알람 변수들.. 건들 ㄴㄴ
    long mNow;
    Date mDate;
    public static int n;
    public static String atclno;
    SimpleDateFormat mFormat = new SimpleDateFormat("yy.MM.dd");

    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    private Context activity;

    SharedPreferences pref;          // 프리퍼런스
    SharedPreferences.Editor editor; // 에디터

    int myInt;                      // 숫자 변수
    String myStr;                   // 문자 변수
    public static boolean noti_tf;    //불린 값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_filter);

        // 1. Shared Preference 초기화
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        // 2. 저장해둔 값 불러오기 ("식별값", 초기값) -> 식별값과 초기값은 직접 원하는 이름과 값으로 작성.
        noti_tf = pref.getBoolean("tf", false); //Boolean 불러오기 (저장해둔 값 없으면 초기값 false로 불러옴)

        dialog = new Dialog(NotiFilter.this);       // Dialog 초기화
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dialog.setContentView(R.layout.dialog);             // xml 레이아웃 파일과 연결

        try {
            user_id = URLEncoder.encode(MainActivity.userID, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final Spinner spin1 = (Spinner)findViewById(R.id.spinner2);
        final Spinner spin2 = (Spinner)findViewById(R.id.spinner3);
        final Spinner spin3 = (Spinner)findViewById(R.id.spinner4);

        Button button_save= (Button) findViewById(R.id.button_save);

        //알림 기능
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mCalender = new GregorianCalendar();

        SwitchButton switchButton = (SwitchButton)findViewById(R.id.sb_use_listener);

        // 4. 앱을 새로 켜면 이전에 저장해둔 값이 표시됨
        switchButton.setChecked(noti_tf);
        if(switchButton.isChecked()){
            setAlarm();
        }

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spin1.setEnabled(true);
                    spin2.setEnabled(true);
                    spin3.setEnabled(true);
                    button_save.setEnabled(true);

                    setAlarm();

                    noti_tf=isChecked;
                    editor.putBoolean("tf", noti_tf);
                    editor.apply(); // 저장

                    //Toast.makeText(NotiFilter.this, "스위치", Toast.LENGTH_SHORT).show();
                }else{
                    spin1.setEnabled(false);
                    spin2.setEnabled(false);
                    spin3.setEnabled(false);
                    button_save.setEnabled(false);

                    noti_tf=isChecked;
                    editor.putBoolean("tf", noti_tf);
                    editor.apply(); // 저장
                }
            }
        });

        //area_filter = (EditText) findViewById(R.id.area_filter);
        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.explain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(); // 아래 showDialog01() 함수 호출
            }
        });


        String[] str = getResources().getStringArray(R.array.spinner_region);
        String[] strr = getResources().getStringArray(R.array.spinner_gu_null);
        String[] strrr = getResources().getStringArray(R.array.spinner_dong_null);

        ArrayAdapter<String> adspin1= new ArrayAdapter<String>(this, R.layout.spinner_item1,str);

        adspin1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin1.setAdapter(adspin1);

        ArrayAdapter<String> adspin2= new ArrayAdapter<String>(this, R.layout.spinner_item2,strr);
        adspin2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin2.setAdapter(adspin2);

        ArrayAdapter<String> adspin3= new ArrayAdapter<String>(this, R.layout.spinner_item3,strrr);
        adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin3.setAdapter(adspin3);

        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(adspin1.getItem(position).equals("서울특별시")){
                    alarmSelect="서울특별시";

                    String[] str2 = getResources().getStringArray(R.array.spinner_region_seoul);
                    ArrayAdapter<String> adspin2= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item2,str2);
                    adspin2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);

                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if(adspin2.getItem(position).equals("강남구")){
                                alarmSelect = "강남구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_seoul_gangnam);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                            else if(adspin2.getItem(position).equals("관악구")){
                                alarmSelect = "관악구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_seoul_gangnam);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }else if(adspin1.getItem(position).equals("경기도")){
                    alarmSelect="경기도";

                    String[] str2 = getResources().getStringArray(R.array.spinner_region_gyeonggi);
                    ArrayAdapter<String> adspin2= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item2,str2);
                    adspin2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spin2.setAdapter(adspin2);

                    spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            if(adspin2.getItem(position).equals("수원시 권선구")){
                                alarmSelect = "수원시 권선구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_suwon1);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                            else if(adspin2.getItem(position).equals("수원시 영통구")){
                                alarmSelect = "수원시 영통구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_suwon2);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                            else if(adspin2.getItem(position).equals("수원시 장안구")){
                                alarmSelect = "수원시 장안구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_suwon3);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                            else if(adspin2.getItem(position).equals("수원시 팔달구")){
                                alarmSelect = "수원시 팔달구";

                                String[] str3 = getResources().getStringArray(R.array.spinner_region_suwon4);
                                ArrayAdapter<String> adspin3= new ArrayAdapter<String>(NotiFilter.this, R.layout.spinner_item3,str3);
                                adspin3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spin3.setAdapter(adspin3);

                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        alarmSelect = adspin3.getItem(position).toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //저장버튼 눌렀을 때

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData("http://118.67.131.208/alarm.php", user_id, alarmSelect);

                //Toast.makeText(NotiFilter.this, alarmSelect, Toast.LENGTH_SHORT).show();
            }
        });


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
    //알림
    //RoutineAlarm.java 여기에 잠들다...
    private void setAlarm() {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(NotiFilter.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotiFilter.this, 0, receiverIntent, 0);
        //String from = "2021-05-16 08:10:30"; //임의로 날짜와 시간을 지정

        getData_user("http://118.67.131.208/getUserFilter.php",user_id);

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

    public void getData_alarm(String url,String area, String date) {


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
                    n=m;
                    atclno = a;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GetDataJSON2 h = new GetDataJSON2();
        h.execute(url);
        //return m;       //여기가 먼저 발생하고 m==이 나중에 발생함.....
    }



    public void getData(String url,String userId,String search) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String search_query = "user_id="+userId+"&search_area="+search;

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
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}