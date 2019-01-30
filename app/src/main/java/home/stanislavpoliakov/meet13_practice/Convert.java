package home.stanislavpoliakov.meet13_practice;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Convert {

    public static String toFormattedZoneDate(Long unixTime, ZoneId timeZone) {
        Date date = new Date(unixTime * 1000);
        Instant instant = date.toInstant();
        ZonedDateTime zonedDate = instant.atZone(timeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return zonedDate.format(formatter);
    }

    public static String toFormattedZoneTime(Long unixTime, ZoneId timeZone) {
        Date time = new Date(unixTime * 1000);
        Instant instant = time.toInstant();
        ZonedDateTime zonedTime = instant.atZone(timeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return zonedTime.format(formatter);
    }

    public static String toPercent(double value) {
        int percent = (int) (value * 100);
        return String.valueOf(percent) + "%";
    }

    public static String toIntensity(double intensity) {
        final double mmInch = 25.4;
        double mmIntencity = intensity * mmInch;
        return String.format(Locale.getDefault(),"%.2f  мм/ч", mmIntencity);
    }

    public static String toCelsius(double fahrenheit) {
        int celsius = (int) Math.round((fahrenheit - 32) * 5 / 9);
        String prefix = (celsius > 0) ? "+" : "";
        return prefix + String.valueOf(celsius) + "˚С";
    }

    public static String toMercury(double pressure) {
        int mercury = (int) Math.round(pressure * 0.750062);
        return String.valueOf(mercury) + " мм.рт.ст.";
    }

    public static String toDirection(double degrees) {
        String chars;
        
        if (degrees >= 337.5 || degrees < 22.5) chars = "С";
        else if (degrees < 67.5) chars = "С-В";
        else if (degrees < 112.5) chars = "В";
        else if (degrees < 157.5) chars = "Ю-В";
        else if (degrees < 202.5) chars = "Ю";
        else if (degrees < 247.5) chars = "Ю-З";
        else if (degrees < 292.5) chars = "З";
        else chars = "С-З";
        
        return chars;
    }

    static String toMeterPerSecond(double speed) {
        double fSpeed = speed / 2.23694;
        return String.format(Locale.getDefault(), "%.1f м/с", fSpeed);
    }
}

