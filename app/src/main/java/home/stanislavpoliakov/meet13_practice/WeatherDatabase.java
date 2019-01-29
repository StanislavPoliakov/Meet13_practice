package home.stanislavpoliakov.meet13_practice;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = Weather.class, version = 2)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDAO getWeatherDAO();
}
