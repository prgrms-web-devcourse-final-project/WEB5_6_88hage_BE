package com.grepp.funfun.app.domain.group.dto.payload;

public class GeocodingResult  {

    private final boolean success;
    private final double latitude;
    private final double longitude;
    private final String guname;

    private GeocodingResult(boolean success, double latitude, double longitude, String guname) {
        this.success = success;
        this.latitude = latitude;
        this.longitude = longitude;
        this.guname = guname;
    }
}
