package cn.alien95.resthttplibrary;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.alien95.resthttp.request.rest.RestHttpRequest;
import cn.alien95.resthttp.request.rest.callback.Callback;
import cn.alien95.resthttp.view.HttpImageView;
import cn.alien95.resthttplibrary.bean.UserInfo;


public class MainActivity extends AppCompatActivity {

    private TextView get, post;
    private HttpImageView smallImage, bigImage;
    private Handler handler = new Handler();
    private static final String IMAGE_SMALL_URL = "http://a2.att.hudong.com/55/63/300000857388127072631279506.jpg";
    private static final String IMAGE_BIG_URL = "http://media01.money4invest.com/2010/04/funny-dog-pictures.jpg";
    private static final String BASE_URL = "http://alien95.cn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        post = (TextView) findViewById(R.id.post);
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
                final UserInfo userInfo = serviceAPI.login("Lemon" , "123456");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(userInfo != null){
                            post.setText(post.getText().toString() + "\n"
                                    + userInfo.toString());
                        }
                    }
                });
//                serviceAPI.login("alien" , "123456");
//                serviceAPI.login("alien95" , "123456");
//                serviceAPI.login("123" , "123456");
//                serviceAPI.login("1234" , "123456");
//                serviceAPI.login("123432" , "123456");
            }
        }).start();

        /**
         * 异步操作，受线程池控制
         */
        serviceAPI.login2("alien95", "123456" ,new Callback<UserInfo>() {
            @Override
            public void callback(UserInfo result) {
                if(result != null){
                    post.setText(post.getText().toString() + "\n"
                            + result.toString());
                }

            }
        });



    }

}
