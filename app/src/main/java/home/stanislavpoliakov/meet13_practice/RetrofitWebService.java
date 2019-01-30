package home.stanislavpoliakov.meet13_practice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitWebService {

    //Токен здесь. Пусть его retrofit подтянет через аннтоцию
    @GET("2028fd6e9ece283ff30f8a5a8f2597db/{locationPoint}")
    Call<Weather> getWeather(@Path("locationPoint") String locationPoint);
}
