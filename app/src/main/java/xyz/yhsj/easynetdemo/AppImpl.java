package xyz.yhsj.easynetdemo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xyz.yhsj.easynet.EasyNet;

/**
 * Created by LOVE on 2016/9/7 007.
 */

public class AppImpl {
    private Context context;
    private AppService appService;
    private Gson gson;

    public AppImpl(Context context) {
        this.context = context;
        appService = EasyNet.getInstance().create(AppService.class);

        gson = new GsonBuilder().
                registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == src.longValue())
                            return new JsonPrimitive(src.longValue());
                        return new JsonPrimitive(src);
                    }
                }).create();
    }


    public Observable<String> baidu() {

        HashMap<String, Object> params = new HashMap<>();
//        params.put("wd", "json");

        return appService.get("api/data/福利/2/1", params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
