package cn.alien95.resthttplibrary;


import cn.alien95.resthttp.request.rest.callback.RestCallback;
import cn.alien95.resthttp.request.rest.method.GET;
import cn.alien95.resthttp.request.rest.method.POST;
import cn.alien95.resthttp.request.rest.param.Field;
import cn.alien95.resthttp.request.rest.param.Query;
import cn.alien95.resthttplibrary.bean.UserInfo;

/**
 * Created by linlongxin on 2016/3/23.
 */
public interface ServiceAPI {

    /**
     * 同步请求方式：不能包含Callback参数
     * @param name
     * @param password
     * @return 返回一个经过Gson解析后的对象
     */

    @POST("/v1/users/login.php")
    UserInfo loginPostSync(@Field("name")
                   String name,
                   @Field("password")
                   String password);

    /**
     * 异步请求：必须有一个Callback参数作为回调
     * @param name
     * @param password
     * @param restCallback  回调泛型类
     */

    @POST("/v1/users/login.php")
    void loginAsyn(@Field("name")
                String name,
                   @Field("password")
                String password, RestCallback<UserInfo> restCallback);

    @GET("/v1/users/login_get.php")
    UserInfo loginGetSync(@Query("name")
                          String name,
                          @Query("password")
                          String password);

    @GET("/v1/users/login_get.php")
    void loginGetAsyn(@Query("name")
                          String name,
                          @Query("password")
                          String password,RestCallback<UserInfo> restCallback);

}
