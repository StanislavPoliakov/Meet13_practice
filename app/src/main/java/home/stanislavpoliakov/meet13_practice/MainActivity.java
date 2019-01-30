package home.stanislavpoliakov.meet13_practice;

import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import home.stanislavpoliakov.meet13_practice.response_data.WDailyData;

public class MainActivity extends AppCompatActivity implements Callback{
    private static final String TAG = "meet13_logs";
    private WeatherDAO dao;
    private MyService networkService;
    private WorkThread workThread = new WorkThread();
    private WDailyData[] data;
    private MyAdapter mAdapter;
    private Map<String, String> cities = new HashMap<>();
    private String cityName;
    private String cityLocation;
    private String timeZone;

    @Override
    public void viewHolderClicked(int itemPosition) {
        WDailyData posData = data[itemPosition];

        Bundle detailInfo = new Bundle();
        detailInfo.putLong("time", posData.time);
        detailInfo.putString("summary", posData.summary);
        detailInfo.putLong("sunriseTime", posData.sunriseTime);
        detailInfo.putLong("sunsetTime", posData.sunsetTime);
        detailInfo.putDouble("precipIntensity", posData.precipIntensity);
        detailInfo.putDouble("precipProbability", posData.precipProbability);
        detailInfo.putDouble("precipIntensityMax", posData.precipIntensityMax);
        detailInfo.putLong("precipIntensityMaxTime", posData.precipIntensityMaxTime);
        detailInfo.putString("precipType", posData.precipType);
        detailInfo.putDouble("dewPoint", posData.dewPoint);
        detailInfo.putDouble("humidity", posData.humidity);
        detailInfo.putDouble("pressure", posData.pressure);
        detailInfo.putDouble("windSpeed", posData.windSpeed);
        detailInfo.putDouble("windBearing", posData.windBearing);
        detailInfo.putDouble("windGust", posData.windGust);
        detailInfo.putDouble("cloudCover", posData.cloudCover);
        detailInfo.putDouble("uvIndex", posData.uvIndex);
        detailInfo.putDouble("temperatureMin", posData.temperatureMin);
        detailInfo.putLong("temperatureMinTime", posData.temperatureMinTime);
        detailInfo.putDouble("temperatureMax", posData.temperatureMax);
        detailInfo.putLong("temperatureMaxTime", posData.temperatureMaxTime);
        detailInfo.putString("timeZone", timeZone);

        Log.d(TAG, "viewHolderClicked: max = " + posData.temperatureMaxTime);
        Log.d(TAG, "viewHolderClicked: min = " + posData.temperatureMinTime);

        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailFragment fragment = DetailFragment.newInstance();
        fragment.setArguments(detailInfo);
        fragmentManager.beginTransaction()
                .add(fragment, "Details")
                .commit();
    }

    /**
     * Поскольку задача состоит из серии последовательных задач с обновлением UI-компонентов, выбор
     * пал на HandlerThread в качестве рабочего потока. Будем реализовывать Task-chaining с
     * последовательным запуском своих же методов через приватный Handler. "Наружу" светят только
     * package-private методы.
     */
    private class WorkThread extends HandlerThread {
        private static final int FETCH_WEATHER_DATA = 1;
        private static final int SAVE_WEATHER_DATA = 2;
        private static final int RETRIEVE_INFO = 3;

        // Важно отметить, что Handler мы специально делаем приватным, чтобы до него нельзя было
        // дотянуться (если перенести в отдельный класс реализацию). То есть Handler только для
        // внутренних задач.
        private Handler mHandler;

        /**
         * В конструкторе понижаем приоритет потока до фоновой задачи
         */
        WorkThread() {
            super("WorkThread", Process.THREAD_PRIORITY_BACKGROUND);
        }

        /**
         * Определяем Handler в методе подготовки Looper'-а
         */
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mHandler = new Handler(getLooper()) {

                /**
                 * Переопределяем поведение при приеме сообщений
                 * @param msg сообщение
                 */
                @Override
                public void handleMessage(Message msg) {
                    Weather weather;
                    Message message;

                    switch (msg.what) {

                        // Сообщение с просьбой начать загрузку данных из внешнего источника через
                        // Service.
                        case FETCH_WEATHER_DATA:
                            weather = getWeatherFromNetwork();
                            message = mHandler.obtainMessage(SAVE_WEATHER_DATA, weather);
                            mHandler.sendMessage(message);
                            break;

                            // После получения данных из Интернета и успешного парсинга сохраняем
                        // данные в базе
                        case SAVE_WEATHER_DATA:
                            weather = (Weather) msg.obj;
                            saveWeatherData(weather);
                            mHandler.sendEmptyMessage(RETRIEVE_INFO);
                            break;

                            // После сохранения в базе данных - получаем содержимое базы, для работы.
                        // Получаем только часть данных, необходимые для работы (посуточный прогноз).
                        // Также получаем timeZone для коррекции даты и времени в зависимости от
                        // выбранного города
                        case RETRIEVE_INFO:
                            weather = dao.getWeather();
                            data = weather.daily.data;
                            timeZone = weather.timezone;
                            updateRecycler(timeZone);
                            break;
                    }
                }
            };
        }

        /**
         * Метод для начала цепочки действий
         */
        void fetchWeather() {
            mHandler.sendEmptyMessage(FETCH_WEATHER_DATA);
        }

        /**
         * Внутренний метод класса для получения данных
         * @return объект данных из Интернета
         */
        private Weather getWeatherFromNetwork() {
            return networkService.getWeatherFromNetwork(cityLocation);
        }

        /**
         * Внутренний метод класса для сохранения данных в базе.
         * Если записей нет - создаем, если есть - обновляем
         * @param weather данные, которые необходимо сохранить
         */
        private void saveWeatherData(Weather weather) {
            Weather currentWeather = dao.getWeather();
            if (currentWeather == null) dao.insert(weather);
            else {
                weather.id = currentWeather.id;
                dao.update(weather);
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

        /**
         * Получаем Binder для взаимодействия с Service'-ом. Service находится в том же приложении,
         * в том же процессе, более того - мы собираемся управлять сервисом рабочим потоком, поэтому
         * выбор для взаимодействия с Service пал на локальный Binder
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            networkService = ((MyService.NetworkBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * Цепляем Service
     */
    @Override
    protected void onResume() {
        super.onResume();
        bindService(MyService.newIntent(this), serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Отцепляем Service
     */
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(serviceConnection);
    }

    /**
     * Метод инициализации значений
     */
    private void init() {
        // Инициализируем базу данных и data access object
        WeatherDatabase database = Room.databaseBuilder(this, WeatherDatabase.class, "weather")
                .fallbackToDestructiveMigration()
                .build();
        dao = database.getWeatherDAO();

        // Инициализируем Map с названиями городов и их координатами
        cities.put("Москва", "55.7522200, 37.6155600");
        cities.put("Владивосток", "43.1056200, 131.8735300");
        cities.put("Бангкок", "13.7539800, 100.5014400");
        cities.put("Бали", "22.6485900, 88.3411500");
        cities.put("Дубай", "25.0657000, 55.1712800");
        cities.put("Санта-Крус-де-Тенерифе", "28.4682400, -16.2546200");
        cities.put("Нью-Йорк", "40.7142700, -74.0059700");

        // Инициализируем Spinner и его адаптер
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<>(cities.keySet()));
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition("Москва")); // по умолчанию "Москва"

        // Если выбрали другой город - начинаем загрузку данных
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityName = (String) spinner.getSelectedItem();
                cityLocation = cities.get(cityName);
                workThread.fetchWeather();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Метод обновления RecyclerView. Запрос на обновление придет из другого Thread'-а, поэтому
    // само обновление явно запускаем в UI-Thread
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
        mAdapter = new MyAdapter(this, data);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
}
