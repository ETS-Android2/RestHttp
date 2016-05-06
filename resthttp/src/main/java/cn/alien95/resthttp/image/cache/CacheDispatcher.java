package cn.alien95.resthttp.image.cache;

import java.util.concurrent.LinkedBlockingDeque;

import cn.alien95.resthttp.image.callback.ImageCallback;
import cn.alien95.resthttp.util.RestHttpLog;
import cn.alien95.resthttp.util.Util;

/**
 * Created by linlongxin on 2016/3/27.
 */
public class CacheDispatcher {

    private LinkedBlockingDeque<ImgRequest> cacheQueue;
    private boolean isCacheQueueEmpty = true;

    public CacheDispatcher() {
        cacheQueue = new LinkedBlockingDeque<>();
    }

    /**
     * 缓存过得图片请求加入缓存队列
     *
     * @param url
     * @param callback
     */
    public void addCacheQueue(String url, ImageCallback callback) {
        cacheQueue.add(new ImgRequest(url, 1, callback));
        if (isCacheQueueEmpty) {
            start();
        }
    }

    public void addCacheQueue(String url, int inSimpleSize, ImageCallback callback) {
        cacheQueue.add(new ImgRequest(url, inSimpleSize, callback));
        if (isCacheQueueEmpty) {
            start();
        }
    }

    public void addCacheQueue(String url, int reqWidth, int reqHeight, ImageCallback callback) {
        cacheQueue.add(new ImgRequest(url, reqWidth, reqHeight, callback));
        if (isCacheQueueEmpty) {
            start();
        }
    }

    public void start() {
        ImgRequest imgRequest;
        while (!cacheQueue.isEmpty()) {
            imgRequest = cacheQueue.poll();

            /**
             * 通过制定图片的宽和高的方式
             */
            if (imgRequest.isControlWidthAndHeight) {
                if (MemoryCache.getInstance().get(imgRequest.url + imgRequest.reqWidth + "/" + imgRequest.reqHeight) != null) {
                    RestHttpLog.i("Get compress picture from memoryCache");
                    imgRequest.callback.callback(MemoryCache.getInstance().get(imgRequest.url + imgRequest.reqWidth + "/" + imgRequest.reqHeight));
                } else {
                    RestHttpLog.i("Get compress picture from diskCache");
                    imgRequest.callback.callback(DiskCache.getInstance().get(Util.getCacheKey(imgRequest.url + imgRequest.reqWidth + "/" + imgRequest.reqHeight)));
                }
                /**
                 * 不进行图片压缩
                 */
            } else if (imgRequest.inSampleSize <= 1) {
                if (MemoryCache.getInstance().get(imgRequest.url) != null) {
                    RestHttpLog.i("Get picture from memoryCache");
                    imgRequest.callback.callback(MemoryCache.getInstance().get(imgRequest.url));
                } else {
                    RestHttpLog.i("Get picture from diskCache");
                    imgRequest.callback.callback(DiskCache.getInstance().get(Util.getCacheKey(imgRequest.url)));
                }
                /**
                 * 通过inSimpleSize参数进行图片压缩
                 */
            } else if (imgRequest.inSampleSize > 1) {
                /**
                 * 压缩图片缓存读取
                 */
                if (MemoryCache.getInstance().get(imgRequest.url + imgRequest.inSampleSize) != null) {
                    RestHttpLog.i("Get compress picture from memoryCache");
                    imgRequest.callback.callback(MemoryCache.getInstance().get(imgRequest.url + imgRequest.inSampleSize));
                } else {
                    RestHttpLog.i("Get compress picture from diskCache");
                    imgRequest.callback.callback(DiskCache.getInstance().get(Util.getCacheKey(imgRequest.url + imgRequest.inSampleSize)));
                }
            }

            isCacheQueueEmpty = false;
        }
        isCacheQueueEmpty = true;
    }


}
