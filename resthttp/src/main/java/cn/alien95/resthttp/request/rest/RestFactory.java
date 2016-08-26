package cn.alien95.resthttp.request.rest;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import cn.alien95.resthttp.request.Method;
import cn.alien95.resthttp.request.Request;
import cn.alien95.resthttp.request.RequestDispatcher;
import cn.alien95.resthttp.request.ServerCache;
import cn.alien95.resthttp.request.ServerCacheDispatcher;
import cn.alien95.resthttp.request.callback.RestCallback;
import cn.alien95.resthttp.request.rest.method.GET;
import cn.alien95.resthttp.request.rest.param.Header;
import cn.alien95.resthttp.request.rest.method.POST;
import cn.alien95.resthttp.request.rest.param.Field;
import cn.alien95.resthttp.request.rest.param.Query;
import cn.alien95.resthttp.util.Util;

/**
 * Created by linlongxin on 2016/3/24.
 */
public class RestFactory {

    /**
     * 通过动态代理，实例化接口
     */
    public Object create(Class<?> clazz) {

        return Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz}, new ServiceAPIHandler());
    }

    /**
     * Builder模式来添加信息
     */
    public static final class Builder {

        private static String baseUrl = "";

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RestFactory build() {
            return new RestFactory();
        }
    }

    class ServiceAPIHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method method, final Object[] args) throws Throwable {
            /**
             * 是否异步，默认同步
             */
            boolean isAsync = false;
            /**
             * 在异步的情况下，callback参数的位置下标记录
             */
            int callbackPosition = 0;

            final Annotation[] annotations = method.getAnnotations();

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Class[] parameterTypes = method.getParameterTypes();

            Object returnObject = null;

            for (final Annotation methodAnnotation : annotations) {

                /**
                 * 请求头
                 */
                if (methodAnnotation instanceof Header) {
                    String headerStr = ((Header) methodAnnotation).value();
                    String[] header = headerStr.split(":");
                    RestHttpConnection.getInstance().addHeader(header[0], header[1]);
                }
                /**
                 * -----------------------------------GET请求处理--------------------------------------------------------
                 */
                else if (methodAnnotation instanceof GET) {

                    StringBuilder url = new StringBuilder(Builder.baseUrl + ((GET) methodAnnotation).value() + "?");

                    for (int i = 0; i < parameterAnnotations.length; i++) {
                        for (int k = 0; k < parameterAnnotations[i].length; k++) {
                            if (parameterAnnotations[i][k] instanceof Query) {
                                url = url.append(((Query) parameterAnnotations[i][k]).value() + "=" + args[i] + "&");
                            }
                        }
                    }

                    url = url.deleteCharAt(url.length() - 1);

                    /**
                     * 判断是否异步处理
                     */
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof RestCallback) {
                            isAsync = true;
                            callbackPosition = i;
                        }
                    }

                    final String urlStr = url.toString();
                    final int finalCallbackPosition = callbackPosition;

                    if (isAsync) {
                        /**
                         * 异步处理任务，判断缓存
                         */
                        Request request = new Request(urlStr, Method.GET, null,
                                ((RestCallback) args[finalCallbackPosition]).getActualClass(),
                                (RestCallback) args[finalCallbackPosition]);
                        if (ServerCache.getInstance().isExistsCache(Util.getCacheKey(urlStr))) {  //存在缓存
                            ServerCacheDispatcher.getInstance().addCacheRequest(request);
                        } else {
                            Log.i("Network", "thread-name:" + Thread.currentThread().getName());
                            RequestDispatcher.getInstance().addRestRequest(request);
                        }
                    } else {
                        /**
                         * 同步处理任务
                         */
                        Request request = new Request(urlStr, Method.GET, null, method.getReturnType());
                        if (ServerCache.getInstance().isExistsCache(Util.getCacheKey(urlStr))) {  //存在缓存

                            returnObject = ServerCacheDispatcher.getInstance().getRestCacheSync(request);
                        } else { //无缓存
                            Log.i("Network", "thread-name:" + Thread.currentThread().getName());
                            returnObject = RestHttpConnection.getInstance().request(request);
                        }
                    }

                } else if (methodAnnotation instanceof POST) {
                    /**
                     * -------------------------------POST请求处理----------------------------------------------------
                     */
                    final Map<String, String> params = new HashMap<>();

                    for (int i = 0; i < parameterAnnotations.length; i++) {
                        Class paramterType = parameterTypes[i]; //这里可以看出每个参数对应一个注解数组，想不通。。。

                        for (int k = 0; k < parameterAnnotations[i].length; k++) {
                            if (parameterAnnotations[i][k] instanceof Field) {
                                params.put(((Field) parameterAnnotations[i][k]).value(), args[i].toString());
                            }
                        }
                    }

                    final String url = Builder.baseUrl + ((POST) methodAnnotation).value();

                    /**
                     * 判断是否异步回调
                     */
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof RestCallback) {
                            isAsync = true;
                            callbackPosition = i;
                        }
                    }

                    /**
                     * 异步处理任务
                     */
                    if (isAsync) {
                        final int finalCallbackPosition = callbackPosition;
                        /**
                         * 判断是否带有缓存,如果有缓存，异步获取缓存
                         */
                        Request request = new Request(url, Method.POST, params,
                                ((RestCallback) args[finalCallbackPosition]).getActualClass(),
                                (RestCallback) args[finalCallbackPosition]);
                        if (ServerCache.getInstance().isExistsCache(Util.getCacheKey(url, params))) {  //存在缓存
                            ServerCacheDispatcher.getInstance().addCacheRequest(request);
                        } else {  //无缓存
                            RequestDispatcher.getInstance().addRestRequest(request);
                        }

                    } else {
                        /**
                         * 同步请求，判断缓存处理
                         */
                        Request request = new Request(url, Method.POST, params, method.getReturnType());
                        if (ServerCache.getInstance().isExistsCache(Util.getCacheKey(url, params))) {  //存在缓存

                            returnObject = ServerCacheDispatcher.getInstance().getRestCacheSync(request);
                        } else {  //无缓存
                            Log.i("Network", "thread-name:" + Thread.currentThread().getName());
                            returnObject = RestHttpConnection.getInstance().request(request);
                        }
                    }

                }
            }
            /**
             * 执行的方法的返回值，如果方法是void，则返回null（默认）
             */
            return returnObject;
        }

    }

}
