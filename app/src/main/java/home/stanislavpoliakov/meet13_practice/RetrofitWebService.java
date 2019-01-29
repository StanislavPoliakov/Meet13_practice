package home.stanislavpoliakov.meet13_practice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitWebService {

    @GET("55.7522200,37.6155600")
    Call<Weather> getWeather();
}
