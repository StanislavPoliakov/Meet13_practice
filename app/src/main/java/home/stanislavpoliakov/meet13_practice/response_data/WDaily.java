package home.stanislavpoliakov.meet13_practice.response_data;

import android.arch.persistence.room.TypeConverters;

/**
 * Один из объектов структуры данных
 */
public class WDaily {
    public String summary;
    public String icon;

    // Конвертируем массив в JSON-строку, чтобы сохранить в SQLite
    @TypeConverters(WDailyDataConverter.class)
    public WDailyData[] data;
}
