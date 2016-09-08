package xyz.yhsj.easynetdemo;

import java.util.HashMap;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by LOVE on 2016/9/7 007.
 */

public interface AppService {

    @GET
    Observable<String> get(@Url String url,
                           @QueryMap HashMap<String, Object> params

    );
}
