package com.care4elders.paramedicalcare.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class GoogleMapsService {

    private final GeoApiContext context;

    public GoogleMapsService(@Value("${google.maps.api-key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public LatLng geocodeAddress(String address) throws ApiException, InterruptedException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
        if (results.length > 0) {
            return results[0].geometry.location;
        }
        throw new RuntimeException("Geocoding failed for address: " + address);
    }

    public double calculateDistance(LatLng origin, LatLng destination) {
        final int R = 6371; // Earth radius in km

        double lat1 = Math.toRadians(origin.lat);
        double lon1 = Math.toRadians(origin.lng);
        double lat2 = Math.toRadians(destination.lat);
        double lon2 = Math.toRadians(destination.lng);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
}