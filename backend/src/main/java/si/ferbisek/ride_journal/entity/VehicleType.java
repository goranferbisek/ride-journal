package si.ferbisek.ride_journal.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VehicleType {
    CAR,
    MOTORCYCLE,
    MOTORHOME,
    TRUCK,
    OTHER;

    @JsonCreator
    public static VehicleType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (VehicleType type : VehicleType.values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + value);
    }
}
