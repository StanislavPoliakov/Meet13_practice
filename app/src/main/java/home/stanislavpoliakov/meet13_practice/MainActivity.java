package home.stanislavpoliakov.meet13_practice;

import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "meet13_logs";
    private WeatherDAO dao;
    private MyService networkService;
    private WorkThread workThread = new WorkThread();
    private WDailyData[] data;
    private MyAdapter mAdapter;

    private class WorkThread extends HandlerThread {
        private static final int FETCH_WEATHER_DATA = 1;
        private static final int SAVE_WEATHER_DATA = 2;
        private static final int RETRIEVE_BRIEF_INFO = 3;
        private static final int RETRIEVE_DETAIL_INFO = 4;
        private Handler mHandler;



        public WorkThread() {
            super("WorkThread", Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mHandler = new Handler(getLooper()) {

                @Override
                public void handleMessage(Message msg) {
                    Weather weather;
                    Message message;
                    switch (msg.what) {
                        case FETCH_WEATHER_DATA:
                            weather = getWeatherFromNetwork();
                            message = mHandler.obtainMessage(SAVE_WEATHER_DATA, weather);
                            mHandler.sendMessage(message);
                            break;
                        case SAVE_WEATHER_DATA:
                            weather = (Weather) msg.obj;
                            saveWeatherData(weather);
                            mHandler.sendEmptyMessage(RETRIEVE_BRIEF_INFO);
                            break;
                        case RETRIEVE_BRIEF_INFO:
                            weather = dao.getWeather();
                            data = weather.daily.data;
                            updateRecycler(weather.timezone);
                            break;
                    }
                }
            };
        }

        public void fetchWeather() {
            mHandler.sendEmptyMessage(FETCH_WEATHER_DATA);
        }

        private Weather getWeatherFromNetwork() {
            return networkService.getWeatherFromNetwork();
        }

        private void saveWeatherData(Weather weather) {
            dao.insert(weather);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        workThread.start();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            networkService = ((MyService.NetworkBinder) service).getService();
            //workThread.doWork();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(MyService.newIntent(this), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    private void init() {
        WeatherDatabase database = Room.databaseBuilder(this, WeatherDatabase.class, "weather")
                .fallbackToDestructiveMigration()
                .build();
        dao = database.getWeatherDAO();

        Button getButton = findViewById(R.id.getButton);
        getButton.setOnClickListener((v) -> {
            workThread.fetchWeather();
        });


    }

    private void updateRecycler(String city) {
        TextView weatherLabel = findViewById(R.id.wetherLabel);
        weatherLabel.setText(city);

        runOnUiThread(() -> {
            if (mAdapter == null) initRecyclerView();
            else mAdapter.setData(data);
        });

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new MyAdapter(data);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
}
