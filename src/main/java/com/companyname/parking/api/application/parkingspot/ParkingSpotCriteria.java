package com.companyname.parking.api.application.parkingspot;

import com.companyname.parking.api.application.base.filter.BooleanFilter;
import com.companyname.parking.api.application.base.filter.LongFilter;
import com.companyname.parking.api.application.base.filter.StringFilter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Criteria class for the ParkingSpot entity. This class is used in ParkingSpotResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /parking-spots?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@Data
@NoArgsConstructor
public class ParkingSpotCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private BooleanFilter isFree;
}
