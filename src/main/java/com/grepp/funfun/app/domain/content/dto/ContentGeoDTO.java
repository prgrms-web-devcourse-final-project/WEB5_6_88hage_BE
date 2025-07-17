package com.grepp.funfun.app.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentGeoDTO {
    private final String originalAddress;
    private final String exactAddress;
    private final String guname;
    private final Double latitude;
    private final Double longitude;
    private final boolean success;

    public static ContentGeoDTO success(String originalAddress, String exactAddress,
                                        String guname, double latitude, double longitude) {
        return new ContentGeoDTO(originalAddress, exactAddress, guname, latitude, longitude, true);
    }

    public static ContentGeoDTO failure(String originalAddress) {
        return new ContentGeoDTO(originalAddress, null, null, null, null, false);
    }

    public String getCombinedAddress() {
        if (exactAddress != null && originalAddress != null) {
            return exactAddress + " " + originalAddress;
        }
        return originalAddress;
    }
}
