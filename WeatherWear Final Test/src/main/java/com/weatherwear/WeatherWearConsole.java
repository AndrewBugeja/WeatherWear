package com.weatherwear;
import com.weatherwear.data.entity.Location;
import com.weatherwear.service.LocationService;
import com.weatherwear.service.WeatherService;

import java.util.Scanner;

public class WeatherWearConsole {

    public static void main (String [] args) throws Exception {
        runWeatherAppConsole();
    }

    public static void runWeatherAppConsole() throws Exception {
        Scanner scanner = new Scanner(System.in);
        LocationService locationService = new LocationService();
        WeatherService weatherService = new WeatherService();

        do {
            System.out.println("WeatherWear.com");
            System.out.println("---------------");
            System.out.println("1. Recommend clothing for current location");
            System.out.println("2. Recommend clothing for future location");
            System.out.println("3. Exit");
            System.out.println("---------------");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    Location loc = locationService.primaryGetMyLoc();
                    weatherService.weatherRunner(loc);
                    break;
                case 2:
                    locationService.locationRunner();
                    break;
                case 3:
                    System.out.println("Exiting WeatherWear.com.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }while(true);
    }
}
