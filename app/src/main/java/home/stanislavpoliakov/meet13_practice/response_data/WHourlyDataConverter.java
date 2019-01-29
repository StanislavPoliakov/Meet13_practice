package home.stanislavpoliakov.meet13_practice.response_data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WHourlyDataConverter {

    @TypeConverter
    public static WHourlyData[] fromString(String json) {
        Type type = new TypeToken<WHourlyData[]>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    @TypeConverter
    public static String fromArray(Object[] array) {
        Gson gson = new Gson();
        return gson.toJson(array);
    }

}
