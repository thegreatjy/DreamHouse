package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//id 중복체크
public class ValidateIdRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://118.67.131.208/ValidateId.php";
    private Map<String,String> map;

    public ValidateIdRequest(String userID, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("id",userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
