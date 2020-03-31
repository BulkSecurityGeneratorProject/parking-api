package com.companyname.parking.api.application.parkingspot;

import com.companyname.parking.api.application.user.UserService;
import com.companyname.parking.api.domain.parkingspot.ParkingSpot;
import com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.parkingspot.ParkingSpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing ParkingSpot.
 */
@Service
@Transactional
public class ParkingSpotApplicationService {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotApplicationService.class);

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingSpotMapper parkingSpotMapper;

    private final UserService userService;

    public ParkingSpotApplicationService(ParkingSpotRepository parkingSpotRepository, ParkingSpotMapper parkingSpotMapper, UserService userService) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSpotMapper = parkingSpotMapper;
        this.userService = userService;
    }

    /**
     * Save a parkingSpot.
     *
     * @param parkingSpotDTO the entity to save
     * @return the persisted entity
     */

    public ParkingSpotDTO save(ParkingSpotDTO parkingSpotDTO) {
        log.debug("Request to save ParkingSpot : {}", parkingSpotDTO);
        ParkingSpot parkingSpot = parkingSpotMapper.toEntity(parkingSpotDTO);
        parkingSpot = parkingSpotRepository.save(parkingSpot);
        return parkingSpotMapper.toDto(parkingSpot);
    }

    /**
     * Get all the parkingSpots.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ParkingSpotDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ParkingSpots");
        return parkingSpotRepository.findAll(pageable)
            .map(parkingSpotMapper::toDto);
    }


    /**
     * Get one parkingSpot by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<ParkingSpotDTO> findOne(Long id) {
        log.debug("Request to get ParkingSpot : {}", id);
        return parkingSpotRepository.findById(id)
            .map(parkingSpotMapper::toDto);
    }

    /**
     * Delete the parkingSpot by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ParkingSpot : {}", id);
        parkingSpotRepository.deleteById(id);
    }

    /**
     * Free up own parkingSpot.
     *
     */
    public void freeUpOwnParkingSpot() {
        log.debug("Request to free up own ParkingSpot");
        userService.getCurrentUser().ifPresent(user ->
                parkingSpotRepository.save(user.getParkingSpot().freeUp()));
    }

    /**
     * Free up own parkingSpot.
     *
     */
    public void holdOwnParkingSpot() {
        log.debug("Request to hold own ParkingSpot");
        userService.getCurrentUser().ifPresent(user ->
                parkingSpotRepository.save(user.getParkingSpot().hold()));
    }

}