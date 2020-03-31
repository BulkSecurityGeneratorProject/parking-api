package com.companyname.parking.api.application.parkingspot;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the ParkingSpot entity.
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParkingSpotDTO implements Serializable {
    Long id;
    @NotEmpty String name;
    boolean isFree;
    @NotNull Long ownedAccountId;
    String ownedAccountLogin;
}
