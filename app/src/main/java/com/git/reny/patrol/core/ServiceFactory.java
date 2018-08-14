package com.git.reny.patrol.core;

import com.git.reny.patrol.api.APIConfig;
import com.git.reny.patrol.api.MyNetInterceptor;
import com.git.reny.patrol.utils.SingletonUtils;
import com.zyctd.mvplib.http.BaseServiceFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by reny on 2017/2/9.
 */

public class ServiceFactory<S> extends BaseServiceFactory<S> {

    private final long DEFAULT_TIMEOUT = 60;//超时时间 60秒

    private ServiceFactory(){}

    @Override
    protected OkHttpClient getOkHttpClient() {
        //定制OkHttp
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.retryOnConnectionFailure(true);

        clientBuilder.cookieJar(SingletonUtils.cookieJar);
        File cacheFile = new File(SingletonUtils.cacheDir, "HttpCache"); // 指定缓存路径
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 指定缓存大小100Mb
        clientBuilder.cache(cache);

        /*InputStream keyStore = MyApplication.getContext().getResources().openRawResource(BuildConfig.DEBUG ? R.raw.zyctdw_debug : R.raw.zyctdw);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, keyStore, "zyctdw");
        clientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        clientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);*/

        clientBuilder.addNetworkInterceptor(new MyNetInterceptor());

        return clientBuilder.build();
    }

    private static class SingletonHolder {
        private static final ServiceFactory INSTANCE = new ServiceFactory();
    }

    public static ServiceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /*@Override
    public void setClientBuilder(OkHttpClient.Builder clientBuilder) {
        clientBuilder.cookieJar(SingletonUtils.cookieJar);
        File cacheFile = new File(SingletonUtils.cacheDir, "HttpCache"); // 指定缓存路径
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); // 指定缓存大小100Mb
        clientBuilder.cache(cache);

        clientBuilder.addNetworkInterceptor(new MyNetInterceptor());
    }*/

    @Override
    public String getDefaultBaseUrl() {
        //默认BaseUrl
        return APIConfig.BASE_URL_DEFAULT;
    }
}
