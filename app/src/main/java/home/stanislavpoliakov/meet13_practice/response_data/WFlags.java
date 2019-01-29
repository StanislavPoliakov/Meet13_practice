package home.stanislavpoliakov.meet13_practice.response_data;

import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

public class WFlags {
    @TypeConverters(WArrayConverter.class)
    public String[] sources;
    @SerializedName("nearest-station")
    public double nearestStation;
    public String units;
}
