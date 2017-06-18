package com.etern.locationscout;

/**
 * Created by etern on 6/12/2017.
 */

public class Geolocation {
    public enum RangeUnit {
        KILO_METER,
        METER
    }

    public double latitude;
    public double longitude;
    public double range;
    public RangeUnit unit;

    private String getRangeUnitString() {
        switch (unit) {
            case KILO_METER:
                return "km";

            case METER:
                return "mi";

            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s%s", latitude, longitude, range, getRangeUnitString());
    }
}
