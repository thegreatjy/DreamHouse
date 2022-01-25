package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//체크리스트를 서버에 저장하기 위한 코드
public class CheckListRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://118.67.131.208/checklist2.php";
    private Map<String,String> map;

    public CheckListRequest(String user_id, int sale_id, int q1, int q2, int q3, int q4, int q5, int q6, int q7, int q8, int q9, int q10, int q11, int q12, int q13, int q14, int q15, int q16, int q17, int q18, int q19, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("user_id", user_id);
        map.put("sale_id", sale_id+"");
        map.put("q1", q1+"");
        map.put("q2", q2+"");
        map.put("q3", q3+"");
        map.put("q4", q4+"");
        map.put("q5", q5+"");
        map.put("q6", q6+"");
        map.put("q7", q7+"");
        map.put("q8", q8+"");
        map.put("q9", q9+"");
        map.put("q10", q10+"");
        map.put("q11", q11+"");
        map.put("q12", q12+"");
        map.put("q13", q13+"");
        map.put("q14", q14+"");
        map.put("q15", q15+"");
        map.put("q16", q16+"");
        map.put("q17", q17+"");
        map.put("q18", q18+"");
        map.put("q19", q19+"");

        System.out.println(q1+" " + q5);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
