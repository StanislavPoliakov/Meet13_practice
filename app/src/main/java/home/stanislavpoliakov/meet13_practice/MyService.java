package home.stanislavpoliakov.meet13_practice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Response;

public class MyService extends Service {
    private static final String TAG = "meet13_logs";
    private NetworkBinder mBinder = new NetworkBinder();

    public static Intent newIntent(Context context) {
        return new Intent(context, MyService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
       // Log.d(TAG, "onBind: ");
        return mBinder;
    }

    public class NetworkBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public Weather getWeatherFromNetwork() {
        Log.d(TAG, "getWeatherFromNetwork: Thread = " + Thread.currentThread());
        Response<Weather> weatherResponse = getWeather();
        return weatherResponse.body();
    }

    private Response<Weather> getWeather() {
        RetrofitHelper helper = new RetrofitHelper();
        try {
            return helper.getService().getWeather().execute();
        } catch (IOException ex) {
            Log.w(TAG, "Response Error ", ex);
        }
        return null;
    }
}
