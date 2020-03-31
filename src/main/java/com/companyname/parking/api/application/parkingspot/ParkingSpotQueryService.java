package com.companyname.parking.api.application.parkingspot;

import com.companyname.parking.api.application.base.QueryService;
import com.companyname.parking.api.domain.parkingspot.ParkingSpot;
import com.companyname.parking.api.domain.parkingspot.ParkingSpot_;
import com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.parkingspot.ParkingSpotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.JoinType;
import java.util.List;

/**
 * Service for executing complex queries for ParkingSpot entities in the database.
 * The main input is a {@link ParkingSpotCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ParkingSpotDTO} or a {@link Page} of {@link ParkingSpotDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ParkingSpotQueryService extends QueryService<ParkingSpot> {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotQueryService.class);

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingSpotMapper parkingSpotMapper;

    public ParkingSpotQueryService(ParkingSpotRepository parkingSpotRepository, ParkingSpotMapper parkingSpotMapper) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSpotMapper = parkingSpotMapper;
    }

    /**
     * Return a {@link List} of {@link ParkingSpotDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ParkingSpotDTO> findByCriteria(ParkingSpotCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ParkingSpot> specification = createSpecification(criteria);
        return parkingSpotMapper.toDto(parkingSpotRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ParkingSpotDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ParkingSpotDTO> findByCriteria(ParkingSpotCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ParkingSpot> specification = createSpecification(criteria);
        return parkingSpotRepository.findAll(specification, page)
            .map(parkingSpotMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ParkingSpotCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ParkingSpot> specification = createSpecification(criteria);
        return parkingSpotRepository.count(specification);
    }

    /**
     * Function to convert ParkingSpotCriteria to a {@link Specification}
     */
    private Specification<ParkingSpot> createSpecification(ParkingSpotCriteria criteria) {
        Specification<ParkingSpot> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ParkingSpot_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ParkingSpot_.name));
            }
            if (criteria.getIsFree() != null) {
                specification = specification.and(buildSpecification(criteria.getIsFree(), ParkingSpot_.isFree));
            }
        }
        return specification;
    }
}
