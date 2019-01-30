package home.stanislavpoliakov.meet13_practice.response_data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Конвертер для сохранения в SQLite массива строк String[] в строку JSON
 */
public class WArrayConverter {

    /**
     * Метод конвертации из строки JSON в массив строк
     * @param json строк
     * @return массив String[]
     */
    @TypeConverter
    public static String[] fromString(String json) {
        Type type = new TypeToken<String[]>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    /**
     * Метод конвертации из массива строк в JSON-строку
     * @param array массив строк String[]
     * @return JSON-строка
     */
    @TypeConverter
    public static String fromArray(Object[] array) {
        Gson gson = new Gson();
        return gson.toJson(array);
    }

}
