package com.weatherwear.service.uiTest;
import com.weatherwear.service.LocationService;
import com.weatherwear.service.WeatherService;
import com.weatherwear.ui.WeatherWearConsole;
import com.weatherwear.data.entity.LocationIdentifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.mockito.Mockito.*;

public class WeatherWearConsoleUnitTest {

    @Mock
    private LocationService mockLocationService;

    @Mock
    private WeatherService mockWeatherService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRunWeatherAppConsole_CurrentLocation() throws Exception {

        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        LocationIdentifier expectedLocation = new LocationIdentifier();

        when(mockLocationService.primaryGetMyLoc()).thenReturn(expectedLocation);

        WeatherWearConsole.runWeatherAppConsole();

        verify(mockLocationService, times(1)).primaryGetMyLoc();
        verify(mockWeatherService, times(1)).weatherRunner(expectedLocation);
    }

    @Test
    public void testRunWeatherAppConsole_FutureLocation() throws Exception {

        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        doNothing().when(mockLocationService).locationRunner();

        WeatherWearConsole.runWeatherAppConsole();

        verify(mockLocationService, times(1)).locationRunner();
    }

    @Test
    public void testRunWeatherAppConsole_Exit() throws Exception {

        String input = "3\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        WeatherWearConsole.runWeatherAppConsole();


    }
}
