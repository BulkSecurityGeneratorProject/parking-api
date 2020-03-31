package com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.parkingspot;

import com.companyname.parking.api.domain.parkingspot.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long>,
        JpaSpecificationExecutor<ParkingSpot> {
}
