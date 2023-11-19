package com.weatherwear.service;
import java.net.URI;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.weatherwear.data.entity.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherService {

    public String getCurrentWeather(Location loc) throws IOException, InterruptedException {

        String location = loc.getLatitude() + "%2C" + loc.getLongitude();

        String weatherApiKey = "885f0710ddmsh848a7201276366dp166bd8jsn6af8aeda1640";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q="+location))
                .header("X-RapidAPI-Key", weatherApiKey)
                .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public void weatherRunner(Location loc) throws IOException, InterruptedException {
        System.out.println("--------------------------------------------");
        String locationWeatherJson = getCurrentWeather(loc);
        getWeather(locationWeatherJson,true);
        System.out.println("--------------------------------------------");
    }
    private void statementParser(boolean hot, boolean raining) {

        String tempmsg1 = !hot ? "cold" : "hot";
        String tempmsg2 = !hot ? "warm" : "light";

        String precipmsg1 = !raining ? "not" : "currently";
        String precipmsg2 = !raining ? "don't" : "do";

        System.out.println("It's " + tempmsg1 +" you should wear " + tempmsg2 + " clothing.");
        System.out.println("It's " + precipmsg1 +" raining so you " + precipmsg2 + " need an umbrella.");
    }

    public void getWeather(String inputJson, Boolean isCurrent) {

        boolean hot = false;
        boolean raining = false;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode parentnode;
        try {
            parentnode = mapper.readTree(inputJson);
            assert parentnode != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(isCurrent){
            double temp = parentnode.get("current").get("temp_c").asDouble();

            if (temp > 15) {
                hot = true;
            }

            if (parentnode.get("current").get("precip_mm").asDouble()>0){
                raining=true;
            }
        }else{
            double temp = parentnode.get("forecast").get("forecastday").get(0).get("day").get("avgtemp_c").asDouble();

            if (temp > 15) {
                hot = true;
            }
            if (parentnode.get("forecast").get("forecastday").get(0).get("day").get("totalprecip_mm").asDouble()>0){
                raining=true;
            }
        }
        statementParser(hot, raining);
    }

}
