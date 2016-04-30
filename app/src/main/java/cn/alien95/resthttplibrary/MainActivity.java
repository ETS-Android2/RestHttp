package cn.alien95.resthttplibrary;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;

import cn.alien95.resthttp.request.HttpRequest;
import cn.alien95.resthttp.request.callback.HttpCallback;
import cn.alien95.resthttp.request.rest.RestHttpRequest;
import cn.alien95.resthttp.request.rest.callback.RestCallback;
import cn.alien95.resthttp.util.RestHttpLog;
import cn.alien95.resthttp.view.HttpImageView;
import cn.alien95.resthttplibrary.bean.UserInfo;


public class MainActivity extends AppCompatActivity {

    private TextView result;
    private HttpImageView smallImage, bigImage;
    private Handler handler = new Handler();
    private static final String IMAGE_SMALL_URL = "http://a2.att.hudong.com/55/63/300000857388127072631279506.jpg";
    private static final String IMAGE_BIG_URL = "http://media01.money4invest.com/2010/04/funny-dog-pictures.jpg";
    private static final String BASE_URL = "http://alien95.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.post);
        smallImage = (HttpImageView) findViewById(R.id.small_image);
        bigImage = (HttpImageView) findViewById(R.id.big_image);

        smallImage.setImageUrlWithCompress(IMAGE_SMALL_URL, 800, 600);
        bigImage.setImageUrl(IMAGE_BIG_URL);

        final RestHttpRequest restHttpRequest = new RestHttpRequest.Builder()
                .baseUrl(BASE_URL)
                .build();

        final ServiceAPI serviceAPI = (ServiceAPI) restHttpRequest.create(ServiceAPI.class);

        /**
         * 同步操作,不受线程池控制，自己处理线程问题
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserInfo userInfo = serviceAPI.login("Lemon", "123456");
                final UserInfo userInfo1 = serviceAPI.loginGetSync("Alien", "123456");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (userInfo != null) {
                            result.setText(result.getText().toString() + "\n POST :       "
                                    + userInfo.toString());
                        }
                        if (userInfo1 != null) {
                            result.setText(result.getText().toString() + "\n GET :       " + userInfo1.toString());
                        } else {
                            RestHttpLog.i("userInfo1为空");
                        }
                    }
                });
            }
        }).start();

        /**
         * 异步操作，受线程池控制
         */
        serviceAPI.loginAsyn("Fuck", "12345", new RestCallback<UserInfo>() {
            @Override
            public void callback(UserInfo result) {
                if (result != null) {
                    MainActivity.this.result.setText(MainActivity.this.result.getText().toString() + "\n POST :       "
                            + result.toString());
                }

            }
        });

        serviceAPI.loginGetAsyn("Fucker", "123456", new RestCallback<UserInfo>() {
            @Override
            public void callback(UserInfo result) {
                if (result != null) {
                    MainActivity.this.result.setText(MainActivity.this.result.getText().toString() + "\n GET :        "
                            + result.toString());
                } else {
                    RestHttpLog.i("result为空");
                }

            }
        });

        HttpRequest.getInstance().get("http://alien95.cn/v1/users/login_get.php", new HttpCallback() {
            @Override
            public void success(String info) {
                result.setText(result.getText().toString() + "\n 通常请求方式...........GET：     " + info);
            }
        });

        HashMap<String, String> params = new HashMap<>();
        params.put("name", "Lemon95");
        params.put("password", "123456");
        HttpRequest.getInstance().post("http://alien95.cn/v1/users/login.php", params, new HttpCallback() {
            @Override
            public void success(String info) {
                result.setText(result.getText().toString() + "\n 通常请求方式...........POST：     " + info);
            }
        });

    }

}
