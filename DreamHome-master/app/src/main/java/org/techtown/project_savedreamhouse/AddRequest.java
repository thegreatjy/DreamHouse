package org.techtown.project_savedreamhouse;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddRequest extends StringRequest{
    //서버 url 설정(php파일 연동)
    final static private String URL="http://118.67.131.208/cart.php";
    private Map<String, String> parameters;

    public AddRequest(String userID, String houseID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters =new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("houseID",houseID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}