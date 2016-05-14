package cn.alien95.resthttp.image;

import cn.alien95.resthttp.image.cache.CacheDispatcher;
import cn.alien95.resthttp.image.cache.DiskCache;
import cn.alien95.resthttp.image.cache.MemoryCache;
import cn.alien95.resthttp.image.callback.ImageCallback;
import cn.alien95.resthttp.util.Util;


/**
 * Created by linlongxin on 2015/12/26.
 */
public class HttpRequestImage {

    private CacheDispatcher cacheDispatcher;
    private ImgRequestDispatcher imgRequestDispatcher;
    private static HttpRequestImage instance;

    private HttpRequestImage() {
        cacheDispatcher = new CacheDispatcher();
        imgRequestDispatcher = new ImgRequestDispatcher();
    }

    /**
     * 获取一个HttpRequestImage实例，这里是单例模式
     *
     * @return
     */
    public static HttpRequestImage getInstance() {
        if (instance == null) {
            synchronized (HttpRequestImage.class) {
                if (instance == null) {
                    instance = new HttpRequestImage();
                }
            }
        }
        return instance;
    }

    /**
     * 从网络请求图片
     *
     * @param url      图片的网络地址
     * @param callBack 回调接口
     */
    public void requestImage(final String url, final ImageCallback callBack) {

        String key = Util.getCacheKey(url);

        if (MemoryCache.getInstance().isExist(key) || DiskCache.getInstance().isExist(key)) {
            cacheDispatcher.addCacheQueue(url, callBack);
        } else {
            imgRequestDispatcher.addImgRequest(url, callBack);
        }
    }

    /**
     * 图片网络请求压缩处理
     * 图片压缩处理的时候内存缓存和硬盘缓存的key是通过url+inSampleSize 通过MD5加密的
     *
     * @param url
     * @param inSampleSize
     * @param callBack
     */
    public void requestImageWithCompress(final String url, final int inSampleSize, final ImageCallback callBack) {

        String key;
        if (inSampleSize <= 1) {  //无压缩

            requestImage(url, callBack);

        } else if (inSampleSize > 1) {

            key = Util.getCacheKey(url + inSampleSize);

            if (MemoryCache.getInstance().isExist(key) || DiskCache.getInstance().isExist(key)) {
                cacheDispatcher.addCacheQueue(url, inSampleSize, callBack);
            } else {
                imgRequestDispatcher.addImgRequestWithCompress(url, inSampleSize, callBack);
            }
        }
    }

    /**
     * 压缩加载图片
     *
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @param callBack
     */
    public void requestImageWithCompress(final String url, final int reqWidth, final int reqHeight, final ImageCallback callBack) {

        String key = Util.getCacheKey(url + reqWidth + "/" + reqHeight);

        if (MemoryCache.getInstance().isExist(key) || DiskCache.getInstance().isExist(key)) {
            cacheDispatcher.addCacheQueue(url, reqWidth, reqHeight, callBack);
        } else {
            imgRequestDispatcher.addImgRequestWithCompress(url, reqWidth, reqHeight, callBack);
        }
    }

}
