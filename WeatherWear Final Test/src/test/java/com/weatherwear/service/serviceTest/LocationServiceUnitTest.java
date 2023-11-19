package com.weatherwear.service.serviceTest;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import com.weatherwear.data.entity.Location;
import com.weatherwear.service.LocationService;
import com.weatherwear.service.WeatherService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LocationServiceUnitTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private Scanner mockScanner;

    @Mock
    private WeatherService mockWeatherService;

    @InjectMocks
    private LocationService locationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        locationService.setWeatherService(mockWeatherService);
    }

    @Test
    public void testPrimaryGetMyLoc_Success() throws IOException, InterruptedException, HttpTimeoutException {
        // Mock the HttpClient behavior for a successful request
        when(mockHttpClient.send(any(), any())).thenReturn((HttpResponse<Object>) CompletableFuture.completedFuture(mockResponse));
        when(mockResponse.body()).thenReturn("{\"lat\": \"12.34\", \"lon\": \"56.78\"}");

        // Call the primaryGetMyLoc method
        Location result = locationService.primaryGetMyLoc();

        // Verify that the HttpClient send method was called
        verify(mockHttpClient, times(1)).send(any(), any());

        // Verify that the result has the expected values
        assertNotNull(result);
        assertEquals("12.34", result.getLatitude());
        assertEquals("56.78", result.getLongitude());
    }

    @Test(expected = HttpTimeoutException.class)
    public void testPrimaryGetMyLoc_Timeout() throws IOException, InterruptedException, HttpTimeoutException {
        when(mockHttpClient.send(any(), any())).thenThrow(HttpTimeoutException.class);
        locationService.primaryGetMyLoc();
    }

    @Test
    public void testParseLoc_Success() throws HttpTimeoutException {
        when(mockResponse.body()).thenReturn("{\"lat\": \"12.34\", \"lon\": \"56.78\"}");

        Location result = locationService.parseLoc(mockResponse);

        assertNotNull(result);
        assertEquals("12.34", result.getLatitude());
        assertEquals("56.78", result.getLongitude());
    }

    @Test
    public void testDateChecker_ValidDate() {
        String validDate = "2023-11-01";
        String result = locationService.dateChecker(validDate);
        assertEquals(validDate, result);
    }

    @Test
    public void testDateChecker_InvalidDate() {
        String invalidDate = "2023-12-01";
        String result = locationService.dateChecker(invalidDate);
        assertNull(result);
    }

    @Test
    public void testDateChecker_NullDate() {
        String result = locationService.dateChecker(null);
        assertNull(result);
    }

    @Test
    public void testGetFutureLocation_Success() throws Exception {
        String location = "ABC";
        String date = "2023-11-01";
        String expectedJson = "{\"example\": \"json\"}";

        when(mockHttpClient.send(any(), any())).thenReturn((HttpResponse<Object>) CompletableFuture.completedFuture(mockResponse));
        when(mockResponse.body()).thenReturn(expectedJson);

        String result = locationService.getFutureLocation(location, date);

        verify(mockHttpClient, times(1)).send(any(), any());
        assertEquals(expectedJson, result);
    }

    @Test(expected = Exception.class)
    public void testGetFutureLocation_InvalidDate() throws Exception {
        String location = "ABC";
        String invalidDate = "2023-12-01";

        locationService.getFutureLocation(location, invalidDate);
    }

    @Test
    public void testLocationRunner_Success() throws Exception {
        String locationInput = "ABC";
        String dateInput = "2023-11-01";
        when(mockScanner.nextLine()).thenReturn(locationInput, dateInput);

        String expectedJson = "{\"example\": \"json\"}";
        when(mockHttpClient.send(any(), any())).thenReturn((HttpResponse<Object>) CompletableFuture.completedFuture(mockResponse));
        when(mockResponse.body()).thenReturn(expectedJson);

        locationService.locationRunner();

        verify(mockWeatherService, times(1)).getWeather(expectedJson, false);
    }
}
