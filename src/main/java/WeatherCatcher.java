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
import org.json.JSONObject;

import java.io.IOException;

public class WeatherCatcher {

    private HttpGet httpGetCurrentWeather;
    private HttpGet httpGetForecast;
    private CloseableHttpClient httpclient;
    private long cacheTimeoutMillis;

    private JSONObject cachedCurrentWeather;
    private long cachedCurrentWeatherMillis = 0;

    private JSONObject cachedForecast;
    private long cachedForecastMillis = 0;


    public WeatherCatcher(String apiKey, String location, String units, int numForecasts, long cacheTimeoutMillis){
        httpclient = HttpClients.createDefault();
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
        this.cacheTimeoutMillis = cacheTimeoutMillis;
    }

    public JSONObject getCurrentWeather() throws IOException {
        if(System.currentTimeMillis() - cachedCurrentWeatherMillis > cacheTimeoutMillis) {
            System.out.println("Updating current weather");
            cachedCurrentWeather = fulfillRequest(httpGetCurrentWeather);
            cachedCurrentWeatherMillis = System.currentTimeMillis();
        }
        return cachedCurrentWeather;
    }

    public JSONObject getForecast() throws IOException {
        if(System.currentTimeMillis() - cachedForecastMillis > cacheTimeoutMillis) {
            System.out.println("Updating forecast");
            cachedForecast = fulfillRequest(httpGetForecast);
            cachedForecastMillis = System.currentTimeMillis();
        }
        return cachedForecast;
    }

    private JSONObject fulfillRequest(HttpGet httpGet) throws IOException {
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(httpGet);
            HttpEntity currentWeatherEntity = httpResponse.getEntity();
            String responseString = EntityUtils.toString(currentWeatherEntity);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),
                        responseString);
            }
            return new JSONObject(responseString);
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                httpResponse.close();
            } catch (IOException e) {
                System.err.println("Could not close HTTP request of current weather");
            }
        }
    }
}
