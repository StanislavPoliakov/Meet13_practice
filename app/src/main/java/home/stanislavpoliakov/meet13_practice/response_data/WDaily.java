package home.stanislavpoliakov.meet13_practice.response_data;

import android.arch.persistence.room.TypeConverters;

public class WDaily {
    public String summary;
    public String icon;

    @TypeConverters(WDailyDataConverter.class)
    public WDailyData[] data;
}
