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


import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.infai.seits.sepl.operators.Config;
import org.infai.seits.sepl.operators.Message;
import org.infai.seits.sepl.operators.OperatorInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Weather implements OperatorInterface {

    protected CloseableHttpClient httpclient;
    protected HttpGet httpGetCurrentWeather;
    protected HttpGet httpGetForecast;
    protected int numForecasts = 1;
    protected String units;

    public Weather() throws IOException {
        Config config = new Config();
        String location = config.getConfigValue("city", "Leipzig");
        units = config.getConfigValue("units", "metric");
        httpclient = HttpClients.createDefault();
        BufferedReader br = new BufferedReader(new FileReader("api-key"));
        String apiKey = br.readLine();
        httpGetCurrentWeather = new HttpGet("http://api.openweathermap.org/data/2.5/weather?" +
                "APPID=" + apiKey +
                "&q=" + location +
                "&units=" + units
        );
        httpGetForecast = new HttpGet("http://api.openweathermap.org/data/2.5/forecast?" +
                "APPID=" + apiKey +
                "&q=" + location +
                "&units=" + units +
                "&cnt=" + numForecasts
        );
    }

    @Override
    public void run(Message message) {
        CloseableHttpResponse currentWeatherResponse = null;
        CloseableHttpResponse forecastResponse = null;
        try {
            //get current weather
            currentWeatherResponse = httpclient.execute(httpGetCurrentWeather);
            if (currentWeatherResponse.getStatusLine().getStatusCode() != 200) {
                throw new HttpResponseException(currentWeatherResponse.getStatusLine().getStatusCode(),
                        currentWeatherResponse.getStatusLine().getReasonPhrase());
            }
            HttpEntity currentWeatherEntity = currentWeatherResponse.getEntity();
            JSONObject currentWeatherJson = new JSONObject(EntityUtils.toString(currentWeatherEntity));

            //Get forecast
            forecastResponse = httpclient.execute(httpGetForecast);
            if (forecastResponse.getStatusLine().getStatusCode() != 200) {
                throw new HttpResponseException(currentWeatherResponse.getStatusLine().getStatusCode(),
                        currentWeatherResponse.getStatusLine().getReasonPhrase());
            }
            HttpEntity forecastEntity = forecastResponse.getEntity();
            JSONObject forecastJson = new JSONObject(EntityUtils.toString(forecastEntity));

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

            System.out.println("Forecast for " + trendDate);

            final double currentAfterAirHumidity = HumidityCalculator.calculateHumidity(insideTemp,
                    currentWeatherTemp, currentWeatherHumidity, !units.equalsIgnoreCase("metric"));

            final double trendAfterAirHumidity = HumidityCalculator.calculateHumidity(insideTemp,
                    trendTemp, trendHumidity, !units.equalsIgnoreCase("metric"));

            message.output("humidityAfterAir", currentAfterAirHumidity);
            message.output("humidityAfterAirTrend", trendAfterAirHumidity);
            message.output("trendDate", trendDate);
            message.output("insideHumidity", insideHumidity);


        } catch (IOException e) {
            System.err.println("Could no get weather data from openweathermap API!");
            System.err.println("Skipping this message...");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                currentWeatherResponse.close();
            } catch (IOException e) {
                System.err.println("Could not close HTTP request of current weather");
            }
            try {
                forecastResponse.close();
            } catch (IOException e) {
                System.err.println("Could not close HTTP request of forecast");
            }
        }
    }

    @Override
    public void config(Message message) {
        message.addInput("temp");
        message.addInput("humidity");
    }
}
