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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "meet13_logs";
    private WeatherDAO dao;
    private MyService networkService;
    private WorkThread workThread = new WorkThread();
    private WDailyData[] data;
    private MyAdapter mAdapter;
    private Map<String, String> cities = new HashMap<>();
    private String cityName;
    private String cityLocation;

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
                    switch (msg.what) {
                        case FETCH_WEATHER_DATA:
                            Weather weather = getWeatherFromNetwork();
                            Log.d(TAG, "handleMessage: tmz = " + weather.timezone);
                            Message message = mHandler.obtainMessage(SAVE_WEATHER_DATA, weather);
                            mHandler.sendMessage(message);
                            break;
                        case SAVE_WEATHER_DATA:
                            Weather weather1 = (Weather) msg.obj;
                            saveWeatherData(weather1);
                            Log.d(TAG, "handleMessage: tmz1 = " + weather1.timezone);
                            mHandler.sendEmptyMessage(RETRIEVE_BRIEF_INFO);
                            break;
                        case RETRIEVE_BRIEF_INFO:
                            Weather weather2 = dao.getWeather();
                            data = weather2.daily.data;
                            Log.d(TAG, "handleMessage: tmz2 = " + weather2.timezone);
                            updateRecycler(weather2.timezone);
                            break;
                    }
                }
            };
        }

        public void fetchWeather() {
            mHandler.sendEmptyMessage(FETCH_WEATHER_DATA);
        }

        private Weather getWeatherFromNetwork() {
            return networkService.getWeatherFromNetwork(cityLocation);
        }

        private void saveWeatherData(Weather weather) {
            Weather currentWeather = dao.getWeather();
            if (currentWeather == null) Log.d(TAG, "saveWeatherData: insert = " + dao.insert(weather));
            else {
                weather.id = currentWeather.id;
                Log.d(TAG, "saveWeatherData: updated = " + dao.update(weather));
            }
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

        cities.put("Москва", "55.7522200, 37.6155600");
        cities.put("Владивосток", "43.1056200, 131.8735300");
        cities.put("Бангкок", "13.7539800, 100.5014400");
        cities.put("Бали", "22.6485900, 88.3411500");
        cities.put("Дубай", "25.0657000, 55.1712800");
        cities.put("Санта-Крус-де-Тенерифе", "28.4682400, -16.2546200");
        cities.put("Нью-Йорк", "40.7142700, -74.0059700");
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(cities.keySet()));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition("Москва"));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityName = (String) spinner.getSelectedItem();
                //Log.d(TAG, "onItemSelected: Name = " + cityName);
                cityLocation = cities.get(cityName);
                /*cityLocation = Double.parseDouble(locationString.substring(0, locationString.indexOf(",")));
                cityLocation = Double.parseDouble(locationString.substring(locationString.indexOf(",") + 1));*/
                //Log.d(TAG, "onItemSelected: lat = " + cityLocation[0] + " / lon = " + cityLocation[1]);
                workThread.fetchWeather();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateRecycler(String city) {
        TextView weatherLabel = findViewById(R.id.wetherLabel);
        weatherLabel.setText(city);

        runOnUiThread(() -> {
            if (mAdapter == null) initRecyclerView();
            else mAdapter.onNewData(data);
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
