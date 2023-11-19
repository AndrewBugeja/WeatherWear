package com.weatherwear.service;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherwear.data.entity.Location;

public class LocationService {
    private final String weatherApiKey = "885f0710ddmsh848a7201276366dp166bd8jsn6af8aeda1640";
    private WeatherService weatherService;

    public Location primaryGetMyLoc() throws IOException, InterruptedException {

        HttpResponse<String> response = null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://weatherapi-com.p.rapidapi.com/ip.json?q=auto%3Aip"))
                    .timeout(Duration.ofSeconds(3))
                    .header("X-RapidAPI-Key", weatherApiKey)
                    .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
          response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(HttpTimeoutException e){
            System.err.println("Connection with primary location service cannot be established...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://ip-api.com/json/24.48.0.1?fields=lat,lon"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }
        return parseLoc(response);
    }

    public Location parseLoc(HttpResponse<String> response) throws HttpTimeoutException {

        if(response == null){
            throw new HttpTimeoutException("Something went wrong with the location service...");
        }
        Location loc = new Location();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode parentnode;
        try {
            parentnode = mapper.readTree(response.body());
            assert parentnode != null;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        String latitude = parentnode.get("lat").asText();
        String longitude = parentnode.get("lon").asText();

        loc.setLatitude(latitude);
        loc.setLongitude(longitude);

        return loc;
    }

    public String getFutureLocation(String location, String dt) throws Exception{
        if(dateChecker(dt)==null){
            throw new Exception("Invalid Date");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/forecast.json?q=metar%3A"+location+"&dt="+dt))
                .header("X-RapidAPI-Key", weatherApiKey)
                .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String dateChecker(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        Date userdate=null;
        try {
            userdate = sdf.parse(date);
        }catch(Exception e){
            System.out.println("exception:" + e);
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +11);
        Date invalidDate = cal.getTime();

        if(userdate.before(invalidDate)){
            return sdf.format(userdate);
        }
        return null;
    }

    public void locationRunner() throws Exception {

        System.out.println("--------------------------------------------");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Airport ICAO Code (eg. LMML, EGLL): ");
        String location = scanner.nextLine();

        System.out.println("Enter date of arrival (eg. 2023-11-11): ");
        String date = scanner.nextLine();

        String inputJson = getFutureLocation(location, date);

        WeatherService ws = new WeatherService();
        ws.getWeather(inputJson,false);
        System.out.println("-----------------");
    }

    public void setWeatherService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

}
