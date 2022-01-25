package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//회원가입
public class RegisterRequest extends StringRequest{
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://118.67.131.208/Register.php";
    private Map<String,String>map;

    public RegisterRequest(String userID, String userPassword, String userName, String userPhone,String userEmail,int userGender,Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("id",userID);
        map.put("pw",userPassword);
        map.put("name",userName);
        map.put("phone",userPhone);
        map.put("email",userEmail);
        map.put("gender",userGender+"");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
