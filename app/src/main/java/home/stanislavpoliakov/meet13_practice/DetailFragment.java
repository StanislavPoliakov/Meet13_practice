package home.stanislavpoliakov.meet13_practice;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends DialogFragment {
    private static final String TAG = "meet13_logs";

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

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

    private void initViews(View view) {
        Bundle args = getArguments();
        String timeZone = args.getString("timeZone");
        StringBuilder builder = new StringBuilder();

        TextView timeView = view.findViewById(R.id.timeView);
        long time = args.getLong("time");
        Date date = new Date(time * 1000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        //dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

        String dateString = dateFormat.format(date);
        timeView.setText(dateString);

        TextView summaryView = view.findViewById(R.id.summaryView);
        summaryView.setText(args.getString("summary"));

        TextView sunriseView = view.findViewById(R.id.sunriseView);
        time = args.getLong("sunriseTime");
        date = new Date(time * 1000);
        dateFormat = new SimpleDateFormat("HH:mm");
        //ZonedDateTime dateTime = ZonedDateTime.of(date.toInstant().)
        ZonedDateTime dateTime = date.toInstant().atZone(ZoneId.of(timeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Log.d(TAG, "initViews: dateTime = " + formatter.format(dateTime));
        dateString = formatter.format(dateTime);
        sunriseView.setText(dateString);

        TextView sunsetView = view.findViewById(R.id.sunsetView);
        time = args.getLong("sunsetTime");
        date = new Date(time * 1000);
        dateTime = date.toInstant().atZone(ZoneId.of(timeZone));
        dateString = formatter.format(dateTime);
        sunsetView.setText(dateString);

        TextView precipType = view.findViewById(R.id.precipType);
        builder.append("Тип осадков: ");
        builder.append(args.get("precipType"));
        precipType.setText(builder.toString());
        builder.setLength(0);

        TextView precip = view.findViewById(R.id.precip);
        builder.append("Вероятность осадков: ");
        builder.append((int)(args.getDouble("precipProbability") * 100) + "%");
        builder.append(", интенсивность: ");
        builder.append(String.format("%.2f", args.getDouble("precipIntensity") * 25.4)).append(" мм/ч");
        precip.setText(builder.toString());
        builder.setLength(0);

        TextView precipMax = view.findViewById(R.id.precipMax);
        builder.append("Максимальное количестов осадков: ");
        builder.append(String.format("%.2f", args.getDouble("precipIntensityMax") * 25.4)).append(" мм/ч");
        builder.append(", в ");
        time = args.getLong("precipIntensityMaxTime");
        date = new Date(time * 1000);
        dateString = dateFormat.format(date);
        builder.append(dateString);
        precipMax.setText(builder.toString());
        builder.setLength(0);

        TextView humDew = view.findViewById(R.id.humDew);
        builder.append("Влажность воздуха: ");
        builder.append((int) (args.getDouble("humidity") * 100) + "%");
        builder.append(", точка росы при ");
        double f = args.getDouble("dewPoint");
        int t =  (int) Math.round((f - 32) * 5 / 9);
        builder.append(t + "˚С");
        humDew.setText(builder.toString());
        builder.setLength(0);

        TextView pressure = view.findViewById(R.id.pressure);
        builder.append("Давление: ");
        builder.append(Math.round(args.getDouble("pressure") * 0.7494) + " мм.рт.ст.");
        pressure.setText(builder.toString());
        builder.setLength(0);

        TextView wind = view.findViewById(R.id.wind);
        int windNum = (int) Math.round(args.getDouble("windBearing") / 22.5);
        String windChars;
        if (windNum >= 15 || windNum < 1) windChars = "С";
        else if (windNum < 3) windChars = "С-В";
        else if (windNum < 5) windChars = "В";
        else if (windNum < 7) windChars = "Ю-В";
        else if (windNum < 9) windChars = "Ю";
        else if (windNum < 11) windChars = "Ю-З";
        else if (windNum < 13) windChars = "З";
        else windChars = "С-З";
        builder.append("Ветер: ").append(windChars);
        builder.append(", ").append(String.format("%.1f", args.getDouble("windSpeed") / 2.23694)).append(" м/с");
        builder.append(", порывы до: ").append(String.format("%.1f", args.getDouble("windGust") / 2.23694)).append(" м/с");
        wind.setText(builder.toString());
        builder.setLength(0);

        TextView cloudy = view.findViewById(R.id.cloudy);
        builder.append("Облачность: ").append((int) Math.round(args.getDouble("cloudCover") * 100)).append("%");
        cloudy.setText(builder.toString());
        builder.setLength(0);

        TextView uvIndex = view.findViewById(R.id.uvIndex);
        builder.append("Ультрафилетовый индекс: ").append(args.getDouble("uvIndex"));
        uvIndex.setText(builder.toString());
        builder.setLength(0);

        TextView tempMax = view.findViewById(R.id.tempMax);
        f = args.getDouble("temperatureMax");
        t = (int) Math.round((f - 32) * 5 / 9);
        String tString = (t > 0) ? ("+" + String.valueOf(t)) : String.valueOf(t);
        time = args.getLong("temperatureMaxTime");
        date = new Date(time * 1000);
        dateTime = date.toInstant().atZone(ZoneId.of(timeZone));
        dateString = formatter.format(dateTime);
        Log.d(TAG, "initViews: max = " + time + " / " + date);
        //dateString = dateFormat.format(date);
        builder.append("Максимальная температура: ").append(tString).append("˚С в ")
                .append(dateString);
        tempMax.setText(builder.toString());
        builder.setLength(0);

        TextView tempMin = view.findViewById(R.id.tempMin);
        f = args.getDouble("temperatureMin");
        t = (int) Math.round((f - 32) * 5 / 9);
        tString = (t > 0) ? ("+" + String.valueOf(t)) : String.valueOf(t);
        time = args.getLong("temperatureMinTime");
        date = new Date(time * 1000);
        dateTime = date.toInstant().atZone(ZoneId.of(timeZone));
        dateString = formatter.format(dateTime);
        Log.d(TAG, "initViews: max = " + time + " / " + date);
        //dateString = dateFormat.format(date);
        builder.append("Минимальная температура: ").append(tString).append("˚С в ")
                .append(dateString);
        tempMin.setText(builder.toString());
        builder.setLength(0);
    }
}
