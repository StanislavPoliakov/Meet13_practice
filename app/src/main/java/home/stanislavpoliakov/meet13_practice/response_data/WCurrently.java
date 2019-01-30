package home.stanislavpoliakov.meet13_practice.response_data;

/**
 * Структура данных, по которой собираем "объект ответа сервера"
 */
public class WCurrently {
    // Текущее время (UNIX)
    public long time;

    // Краткая сводка
    public String summary;

    // Пиктограмма
    public String icon;

    // Интенсивность осадков (дюйм в час)
    public double precipIntensity;

    // Вероятность осадков
    public double precipProbability;

    // Тип осадков (snow, rain...)
    public String precipType;

    // Текущая температура (фаренгейт)
    public double temperature;

    // Температура "ощущается как" (фаренгейт)
    public double apparentTemperature;

    // Точка росы (фаренгейт)
    public double dewPoint;

    // Относительная влажность
    public double humidity;

    // Давление (миллибары)
    public double pressure;

    // Скорость ветра (мили в час)
    public double windSpeed;

    // Порывы ветра до (мили в час)
    public double windGust;

    // Направление ветра из (градусы)
    public double windBearing;

    // Облачность
    public double cloudCover;

    // Ультрафиолетовый индекс
    public double uvIndex;

    // Средняя видимость, ограниченная 10 милями
    public double visibility;

    // Плотность атмосферного озона в единицах Добсона
    public double ozone;
}
