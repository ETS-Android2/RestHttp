package cn.lemon.restdemo.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import cn.alien95.restdemo.R;
import cn.lemon.resthttp.request.callback.HttpCallback;
import cn.lemon.resthttp.request.http.HttpRequest;
import cn.lemon.restdemo.data.Config;

public class HttpActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mGet,mPost;
    private TextView mResult;

    private final String GET_URL = Config.HOST + "course/v1/accounts/banner.php";
    private final String POST_URL = Config.HOST + "course/v1/courses/starJCourseList.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mGet = (Button) findViewById(R.id.get);
        mPost = (Button) findViewById(R.id.post);
        mResult = (TextView) findViewById(R.id.result);
        mGet.setOnClickListener(this);
        mPost.setOnClickListener(this);
    }

    public void get(){
        mResult.setText("");
        HttpRequest.getInstance().get(GET_URL, new HttpCallback() {
            @Override
            public void success(String info) {
                mResult.setText(new Gson().toJson(info));
            }
        });
    }

    public void post(){
        mResult.setText("");
        Map<String,String> params = new HashMap<>();
        params.put("page","0");
        HttpRequest.getInstance().addHeader("UID","1");
        HttpRequest.getInstance().addHeader("token","9ba712a6210728364ea7c2d7457cde");
        HttpRequest.getInstance().post(POST_URL, params,new HttpCallback() {
            @Override
            public void success(String info) {
                mResult.setText(new Gson().toJson(info));
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.get:
                get();
                break;
            case R.id.post:
                post();
                break;
        }
    }
}
