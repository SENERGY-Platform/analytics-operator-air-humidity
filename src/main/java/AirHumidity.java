/*
 * Copyright 2018 InfAI (CC SES)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.infai.seits.sepl.operators.Config;
import org.infai.seits.sepl.operators.Message;
import org.infai.seits.sepl.operators.OperatorInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class AirHumidity implements OperatorInterface {

    protected WeatherCatcher weatherCatcher;
    protected int numForecasts = 1;
    protected String units;

    public AirHumidity() throws IOException {
        Config config = new Config();
        String location = config.getConfigValue("city", "Leipzig");
        units = config.getConfigValue("units", "metric");
        BufferedReader br = new BufferedReader(new FileReader("api-key"));
        String apiKey = br.readLine();
        long cacheTimeoutMillis = 1000 * 60 * 10; //10min, openweathermap update interval
        weatherCatcher = new WeatherCatcher(apiKey, location, units, numForecasts, cacheTimeoutMillis);
    }

    @Override
    public void run(Message message) {
        JSONObject currentWeatherJson;
        JSONObject forecastJson;
        try {
            currentWeatherJson = weatherCatcher.getCurrentWeather();
            forecastJson = weatherCatcher.getForecast();
        } catch (IOException e) {
            System.err.println("Could no get weather data from openweathermap API!");
            System.err.println("Skipping this message...\n");
            e.printStackTrace();
            return;
        }

        //Get inside values
        final Double insideTemp = message.getInput("temp").getValue();
        final Double insideHumidity = message.getInput("humidity").getValue();

        //Extract current weather data
        final double currentWeatherTemp = currentWeatherJson.getJSONObject("main").getDouble("temp");
        final double currentWeatherHumidity = currentWeatherJson.getJSONObject("main").getDouble("humidity");

        //Extract forecast weather data
        JSONArray list = forecastJson.getJSONArray("list");

        final double trendHumidity = list.getJSONObject(0).getJSONObject("main").getDouble("humidity");
        final double trendTemp = list.getJSONObject(0).getJSONObject("main").getDouble("temp");
        String trendDate = list.getJSONObject(0).getString("dt_txt");

        final double currentAfterAirHumidity = HumidityCalculator.calculateHumidity(insideTemp,
                currentWeatherTemp, currentWeatherHumidity, !units.equalsIgnoreCase("metric"));

        final double trendAfterAirHumidity = HumidityCalculator.calculateHumidity(insideTemp,
                trendTemp, trendHumidity, !units.equalsIgnoreCase("metric"));

        message.output("humidityAfterAir", currentAfterAirHumidity);
        message.output("humidityAfterAirTrend", trendAfterAirHumidity);
        message.output("trendDate", trendDate);
        message.output("insideHumidity", insideHumidity);
    }

    @Override
    public void config(Message message) {
        message.addInput("temp");
        message.addInput("humidity");
    }
}
