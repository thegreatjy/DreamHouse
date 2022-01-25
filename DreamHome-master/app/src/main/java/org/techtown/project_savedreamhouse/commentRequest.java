package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//커뮤니티 글 댓글 작성
public class commentRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://118.67.131.208/comment_insert.php";
    private Map<String,String> map;

    public commentRequest(int articleID, String user_id, String contents, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("articleID", articleID+"");
        map.put("writerID", user_id);
        map.put("contents", contents);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}