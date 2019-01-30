package home.stanislavpoliakov.meet13_practice;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.ZoneId;
import java.util.Date;

import static home.stanislavpoliakov.meet13_practice.Convert.toCelsius;
import static home.stanislavpoliakov.meet13_practice.Convert.toDirection;
import static home.stanislavpoliakov.meet13_practice.Convert.toFormattedZoneTime;
import static home.stanislavpoliakov.meet13_practice.Convert.toFormattedZoneDate;
import static home.stanislavpoliakov.meet13_practice.Convert.toIntensity;
import static home.stanislavpoliakov.meet13_practice.Convert.toMercury;
import static home.stanislavpoliakov.meet13_practice.Convert.toMeterPerSecond;
import static home.stanislavpoliakov.meet13_practice.Convert.toPercent;


/**
 * Класс фрагмента детальной информации
 */
public class DetailFragment extends DialogFragment {
    private static final String TAG = "meet13_logs";
    private ZoneId timeZone;

    /**
     * Получаем объект класса в статическом методе для FragmentManager
     * @return
     */
    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    /**
     * Растягиваем фрагмент по ширине на размер экрана, по высоте - на размер переменных
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }



    /**
     * Инициализируем и наполняем UI-компоненты
     * @param view, в которой эти компоненты представлены
     */
    private void initViews(View view) {
        long unixTime;
        String timeString;
        double value, intensity, f;

        // Bundle с подробной информацией о погоде, переданный в качестве аргументов фрагмента
        Bundle args = getArguments();

        // Временная зона по IANA Time Zone Database
        timeZone = ZoneId.of(args.getString("timeZone"));
        StringBuilder builder = new StringBuilder();

        // Элемент текущей даты
        TextView timeView = view.findViewById(R.id.timeView);
        unixTime = args.getLong("time");
        timeView.setText(toFormattedZoneDate(unixTime, timeZone));

        // Элемент краткой сводки (на английском)
        TextView summaryView = view.findViewById(R.id.summaryView);
        String summary = args.getString("summary");
        summaryView.setText(summary);

        // Время восхода солнца
        TextView sunriseView = view.findViewById(R.id.sunriseView);
        unixTime = args.getLong("sunriseTime");
        sunriseView.setText(toFormattedZoneTime(unixTime, timeZone));

        // Время заката солнца
        TextView sunsetView = view.findViewById(R.id.sunsetView);
        unixTime = args.getLong("sunsetTime");
        sunsetView.setText(toFormattedZoneTime(unixTime, timeZone));

        // Тип осадков
        TextView precipTypeView = view.findViewById(R.id.precipType);
        String precipType = args.getString("precipType");
        builder.append("Тип осадков: ")
                .append(precipType);
        precipTypeView.setText(builder.toString());
        builder.setLength(0);

        // Вероятность осадков и их интенсивность
        TextView precip = view.findViewById(R.id.precip);
        value = args.getDouble("precipProbability");
        intensity = args.getDouble("precipIntensity");
        builder.append("Вероятность осадков: ")
                .append(toPercent(value))
                .append(", интенсивность: ")
                .append(toIntensity(intensity));
        precip.setText(builder.toString());
        builder.setLength(0);

        // В какое время максимальное количество осадков
        TextView precipMax = view.findViewById(R.id.precipMax);
        intensity = args.getDouble("precipIntensityMax");
        unixTime = args.getLong("precipIntensityMaxTime");
        builder.append("Максимальное количестов осадков: ")
                .append(toIntensity(intensity))
                .append(", в ")
                .append(toFormattedZoneTime(unixTime, timeZone));
        precipMax.setText(builder.toString());
        builder.setLength(0);

        // Влажность воздуха и температура точки росы
        TextView humDew = view.findViewById(R.id.humDew);
        value = args.getDouble("humidity");
        f = args.getDouble("dewPoint");
        builder.append("Влажность воздуха: ")
                .append(toPercent(value))
                .append(", точка росы при ")
                .append(toCelsius(f));
        humDew.setText(builder.toString());
        builder.setLength(0);

        // Давление
        TextView pressure = view.findViewById(R.id.pressure);
        value = args.getDouble("pressure");
        builder.append("Давление: ")
                .append(toMercury(value));
        pressure.setText(builder.toString());
        builder.setLength(0);

        // Направление ветра, скорость и скорость в порывах
        TextView wind = view.findViewById(R.id.wind);
        double bearing = args.getDouble("windBearing");
        double speed = args.getDouble("windSpeed");
        double gust = args.getDouble("windGust");
        builder.append("Ветер: ")
                .append(toDirection(bearing))
                .append(", ")
                .append(toMeterPerSecond(speed))
                .append(", порывы до: ")
                .append(toMeterPerSecond(gust));
        wind.setText(builder.toString());
        builder.setLength(0);

        // Облачность
        TextView cloudy = view.findViewById(R.id.cloudy);
        value = args.getDouble("cloudCover");
        builder.append("Облачность: ")
                .append(toPercent(value));
        cloudy.setText(builder.toString());
        builder.setLength(0);

        // Ультрафиолетовый индекс
        TextView uvIndex = view.findViewById(R.id.uvIndex);
        value = args.getDouble("uvIndex");
        builder.append("Ультрафилетовый индекс: ")
                .append(value);
        uvIndex.setText(builder.toString());
        builder.setLength(0);

        // Максимальная температура и время пика
        TextView tempMax = view.findViewById(R.id.tempMax);
        f = args.getDouble("temperatureMax");
        unixTime = args.getLong("temperatureMaxTime");
        builder.append("Максимальная температура: ")
                .append(toCelsius(f))
                .append(" в ")
                .append(toFormattedZoneTime(unixTime, timeZone));
        tempMax.setText(builder.toString());
        builder.setLength(0);

        // Минимальная температура и время пика
        TextView tempMin = view.findViewById(R.id.tempMin);
        f = args.getDouble("temperatureMin");
        unixTime = args.getLong("temperatureMinTime");
        builder.append("Минимальная температура: ")
                .append(toCelsius(f))
                .append(" в ")
                .append(toFormattedZoneTime(unixTime, timeZone));
        tempMin.setText(builder.toString());
        builder.setLength(0);
    }
}
