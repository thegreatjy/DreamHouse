package org.techtown.project_savedreamhouse;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//저장된 체크리스트를 서버에서 불러오는 코드
public class CheckListLoad extends StringRequest {
    // 서버 url 설정(php파일 연동)
    final static private String URL = "http://118.67.131.208/phphp.php";
    private Map<String, String> map;

    public CheckListLoad(String user_id, int sale_id, Response.Listener<String>listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("sale_id", sale_id+"");
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
