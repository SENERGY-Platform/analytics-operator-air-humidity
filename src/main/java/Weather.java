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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Weather implements OperatorInterface {

    protected CloseableHttpClient httpclient;
    protected HttpGet httpGet;

    public Weather() throws IOException {
        Config config = new Config();
        String location = config.getConfigValue("city", "Leipzig");
        String units = config.getConfigValue("units", "metric");
        httpclient = HttpClients.createDefault();
        BufferedReader br = new BufferedReader(new FileReader("api-key"));
        String apiKey = br.readLine();
        httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?" +
                "APPID=" + apiKey +
                "&q=" + location +
                "&units=" + units
        );
    }

    @Override
    public void run(Message message) {
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new HttpResponseException(response.getStatusLine().getStatusCode(),
                        response.getStatusLine().getReasonPhrase());
            }
            HttpEntity entity = response.getEntity();
            JSONObject json = new JSONObject(EntityUtils.toString(entity));
            message.output("web-temp", json.getJSONObject("main").getDouble("temp"));
            message.output("web-humidity", json.getJSONObject("main").getDouble("humidity"));
            message.output("device-temp", message.getInput("temp").getValue());
            message.output("device-humidity", message.getInput("humidity").getValue());
        } catch (IOException e) {
            System.err.println("Could no get weather data from openweathermap API!");
            System.err.println("Skipping this message...");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void config(Message message) {
        message.addInput("temp");
        message.addInput("humidity");
    }
}
