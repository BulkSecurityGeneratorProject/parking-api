package com.companyname.parking.api.application.parkingspot;

import com.companyname.parking.api.application.base.EntityMapper;
import com.companyname.parking.api.application.user.UserMapperResolver;
import com.companyname.parking.api.domain.parkingspot.ParkingSpot;
import com.companyname.parking.api.domain.user.User;
import org.mapstruct.*;

/**
 * Mapper for the entity ParkingSpot and its DTO ParkingSpotDTO.
 */
@Mapper(componentModel = "spring", uses = { UserMapperResolver.class })
public interface ParkingSpotMapper extends EntityMapper<ParkingSpotDTO, ParkingSpot> {

    @Mapping(source = "ownedAccount.id", target = "ownedAccountId")
    @Mapping(source = "ownedAccount.login", target = "ownedAccountLogin")
    ParkingSpotDTO toDto(ParkingSpot parkingSpot);

    @Mapping(source = "ownedAccountId", target = "ownedAccount")
    ParkingSpot toEntity(ParkingSpotDTO parkingSpotDTO);

    User map(Long value);

    default ParkingSpot fromId(Long id) {
        if (id == null) {
            return null;
        }
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(id);
        return parkingSpot;
    }
}
