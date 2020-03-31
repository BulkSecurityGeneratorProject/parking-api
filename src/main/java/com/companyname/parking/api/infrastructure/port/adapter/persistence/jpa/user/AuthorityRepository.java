package com.companyname.parking.api.infrastructure.port.adapter.persistence.jpa.user;

import com.companyname.parking.api.domain.user.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
