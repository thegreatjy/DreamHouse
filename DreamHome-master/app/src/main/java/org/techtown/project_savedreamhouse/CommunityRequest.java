package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//커뮤니티 글 작성
public class CommunityRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://118.67.131.208/community.php";
    private Map<String,String> map;

    public CommunityRequest(int id, String user_id, String title, String category, String contents, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("id", id+"");
        map.put("userID", user_id);
        map.put("title", title);
        map.put("category", category);
        map.put("contents", contents);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
