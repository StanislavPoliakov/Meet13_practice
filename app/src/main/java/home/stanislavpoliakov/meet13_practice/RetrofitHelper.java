package home.stanislavpoliakov.meet13_practice;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static final String TAG = "meet13_logs";
    private static final String BASE_URL = "https://api.darksky.net/forecast/";
    private static final String TOKEN = "2028fd6e9ece283ff30f8a5a8f2597db/";
    //private static final String MOSCOW = "55.7522200,37.6155600/";

    public RetrofitWebService getService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        //Log.d(TAG, "getService: Thread = " + Thread.currentThread());
        return retrofit.create(RetrofitWebService.class);
    }
}
