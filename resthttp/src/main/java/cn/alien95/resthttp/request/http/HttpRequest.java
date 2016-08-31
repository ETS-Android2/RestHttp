package cn.alien95.resthttp.request.http;

import java.util.Map;

import cn.alien95.resthttp.request.Method;
import cn.alien95.resthttp.request.Request;
import cn.alien95.resthttp.request.RequestDispatcher;
import cn.alien95.resthttp.request.RestHttp;
import cn.alien95.resthttp.request.ServerCache;
import cn.alien95.resthttp.request.ServerCacheDispatcher;
import cn.alien95.resthttp.request.callback.HttpCallback;
import cn.alien95.resthttp.util.Util;


/**
 * Created by linlongxin on 2015/12/26.
 */
public class HttpRequest extends RestHttp {

    public static HttpRequest getInstance(){
        return getInstance(HttpRequest.class);
    }

    public void addHeader(Map<String, String> header) {
        HttpConnection.getInstance().addHeader(header);
    }

    public void addHeader(String key, String value) {
        HttpConnection.getInstance().addHeader(key, value);
    }

    /**
     * GET
     */
    @Override
    public void get(final String url, final HttpCallback callBack) {
        /**
         * 缓存判断
         */
        Request request = new Request(url, Method.GET, null, callBack);
        httpRequest(request);
    }

    /**
     * POST
     */
    @Override
    public void post(final String url, final Map<String, String> params, final HttpCallback callBack) {
        /**
         * 缓存判断
         */
        Request request = new Request(url, Method.POST, params, callBack);
        httpRequest(request);
    }

    public void cancelAllRequest() {
        RequestDispatcher.getInstance().cancelAllNetRequest();
        RequestDispatcher.getInstance().cancelAllImageRequest();
    }

    public void cancelRequest(String httpUrl, Map<String, String> params) {
        RequestDispatcher.getInstance().cancelRequest(httpUrl, params);
    }

    public void cancelRequest(String httpUrl) {
        RequestDispatcher.getInstance().cancelRequest(httpUrl);
    }

    private void httpRequest(Request request){
        if (ServerCache.getInstance().isExistsCache(Util.getCacheKey(request.url))) {
            ServerCacheDispatcher.getInstance().addCacheRequest(request);
        } else
            RequestDispatcher.getInstance().addHttpRequest(request);
    }
}
