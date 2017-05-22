package cn.lemon.resthttp.image;

import cn.lemon.resthttp.image.cache.ImageRequest;
import cn.lemon.resthttp.request.RequestDispatcher;
import cn.lemon.resthttp.util.HttpLog;
import cn.lemon.resthttp.util.RestHttpLog;

/**
 * Created by linlongxin on 2016/4/26.
 */
public class ImageRequestDispatcher {

    /**
     * 添加到线程池请求队列
     */
    public void addImageRequest(ImageRequest request) {
        RestHttpLog.i("Get picture from network");
        HttpLog.requestImageLog(request.url);

        RequestDispatcher.getInstance().addImageRequest(request);
    }
}
