package com.weatherwear.service.serviceTest;

import com.weatherwear.data.entity.LocationIdentifier;
import com.weatherwear.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WeatherServiceUnitTest {
    private WeatherService weatherService;
    private HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        httpClient = mock(HttpClient.class);
        weatherService = new WeatherService();
    }

    @Test
    public void testgetCurrentWeather() throws IOException, InterruptedException {
        LocationIdentifier location = new LocationIdentifier();
        location.setLatitude("12.34");
        location.setLongitude("56.78");

        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"current\":{\"temp_c\":20,\"precip_mm\":0}}");

        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);

        String weatherData = weatherService.getCurrentWeather(location);

        assertNotNull(weatherData);

    }

    @Test
    public void testWeatherRunner() throws IOException, InterruptedException {
        LocationIdentifier location = new LocationIdentifier();
        location.setLatitude("12.34");
        location.setLongitude("56.78");

        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("{\"current\":{\"temp_c\":20,\"precip_mm\":0}}");

        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockResponse);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        weatherService.weatherRunner(location);

        System.setOut(System.out);

        String expectedOutput = "--------------------------------------------\n" +
                "It is warm you should wear light clothing.\n" +
                "It is not raining so you don't need an umbrella.\n" +
                "--------------------------------------------\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}
