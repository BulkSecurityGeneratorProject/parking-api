package com.companyname.parking.api.infrastructure.port.adapter.rest.parkingspot;

import com.companyname.parking.api.application.parkingspot.ParkingSpotApplicationService;
import com.companyname.parking.api.application.parkingspot.ParkingSpotCriteria;
import com.companyname.parking.api.application.parkingspot.ParkingSpotDTO;
import com.companyname.parking.api.application.parkingspot.ParkingSpotQueryService;
import com.companyname.parking.api.application.user.AuthoritiesConstants;
import com.companyname.parking.api.infrastructure.port.adapter.rest.errors.BadRequestAlertException;
import com.companyname.parking.api.infrastructure.port.adapter.rest.util.HeaderUtil;
import com.companyname.parking.api.infrastructure.port.adapter.rest.util.PaginationUtil;
import com.companyname.parking.api.infrastructure.port.adapter.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ParkingSpot.
 */
@RestController
@RequestMapping
public class ParkingSpotResource {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotResource.class);

    private static final String ENTITY_NAME = "parkingSpot";

    private final ParkingSpotApplicationService parkingSpotApplicationService;

    private final ParkingSpotQueryService parkingSpotQueryService;

    public ParkingSpotResource(ParkingSpotApplicationService parkingSpotApplicationService, ParkingSpotQueryService parkingSpotQueryService) {
        this.parkingSpotApplicationService = parkingSpotApplicationService;
        this.parkingSpotQueryService = parkingSpotQueryService;
    }

    /**
     * POST  /parking-spots : Create a new parkingSpot.
     *
     * @param parkingSpotDTO the parkingSpotDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new parkingSpotDTO, or with status 400 (Bad Request) if the parkingSpot has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(path = "/parking-spots", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<ParkingSpotDTO> createParkingSpot(@Valid @RequestBody ParkingSpotDTO parkingSpotDTO) throws URISyntaxException {
        log.debug("REST request to save ParkingSpot : {}", parkingSpotDTO);
        if (parkingSpotDTO.getId() != null) {
            throw new BadRequestAlertException("A new parkingSpot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParkingSpotDTO result = parkingSpotApplicationService.save(parkingSpotDTO);
        return ResponseEntity.created(new URI("/api/parking-spots/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /parking-spots : Updates an existing parkingSpot.
     *
     * @param parkingSpotDTO the parkingSpotDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated parkingSpotDTO,
     * or with status 400 (Bad Request) if the parkingSpotDTO is not valid,
     * or with status 500 (Internal Server Error) if the parkingSpotDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping(path = "/parking-spots", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<ParkingSpotDTO> updateParkingSpot(@Valid @RequestBody ParkingSpotDTO parkingSpotDTO) throws URISyntaxException {
        log.debug("REST request to update ParkingSpot : {}", parkingSpotDTO);
        if (parkingSpotDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ParkingSpotDTO result = parkingSpotApplicationService.save(parkingSpotDTO);
        return ResponseEntity.ok()
                .body(result);
    }

    /**
     * GET  /parking-spots : get all the parkingSpots.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of parkingSpots in body
     */
    @GetMapping(path = "/parking-spots", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<List<ParkingSpotDTO>> getAllParkingSpots(ParkingSpotCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ParkingSpots by criteria: {}", criteria);
        Page<ParkingSpotDTO> page = parkingSpotQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/parking-spots");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /parking-spots/count : count all the parkingSpots.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the count in body
     */
    @GetMapping(path = "/parking-spots/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAnyRole(\"" + AuthoritiesConstants.USER + "\", \"" + AuthoritiesConstants.GATE + "\")")
    public ResponseEntity<Long> countParkingSpots(ParkingSpotCriteria criteria) {
        log.debug("REST request to count ParkingSpots by criteria: {}", criteria);
        return ResponseEntity.ok().body(parkingSpotQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /parking-spots/:id : get the "id" parkingSpot.
     *
     * @param id the id of the parkingSpotDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the parkingSpotDTO, or with status 404 (Not Found)
     */
    @GetMapping(value = "/parking-spots/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<ParkingSpotDTO> getParkingSpot(@PathVariable Long id) {
        log.debug("REST request to get ParkingSpot : {}", id);
        Optional<ParkingSpotDTO> parkingSpotDTO = parkingSpotApplicationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parkingSpotDTO);
    }

    /**
     * DELETE  /parking-spots/:id : delete the "id" parkingSpot.
     *
     * @param id the id of the parkingSpotDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/parking-spots/{id}")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\")")
    public ResponseEntity<Void> deleteParkingSpot(@PathVariable Long id) {
        log.debug("REST request to delete ParkingSpot : {}", id);
        parkingSpotApplicationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * POST  /parking-spots/freeUp : Free own parkingSpot.
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/parking-spots/freeUp")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.PARKING_SPOT + "\")")
    public ResponseEntity<Void> freeUpOwnParkingSpot() {
        log.debug("REST request to free up own ParkingSpot.");

        parkingSpotApplicationService.freeUpOwnParkingSpot();
        return ResponseEntity.ok().build();
    }

    /**
     * POST  /parking-spots/hold : Hold own parkingSpot.
     *
     * @return the ResponseEntity with status 200 (OK)
     */
    @PostMapping("/parking-spots/hold")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.PARKING_SPOT + "\")")
    public ResponseEntity<Void> holdOwnParkingSpot() {
        log.debug("REST request to hold own ParkingSpot.");

        parkingSpotApplicationService.holdOwnParkingSpot();
        return ResponseEntity.ok().build();
    }

}
