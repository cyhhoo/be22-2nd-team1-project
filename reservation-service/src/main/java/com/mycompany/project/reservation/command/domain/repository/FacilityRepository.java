package com.mycompany.project.reservation.command.domain.repository;

import com.mycompany.project.reservation.command.domain.aggregate.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

  boolean existsByName(String name);

  boolean existsByNameAndFacilityIdNot(String name, Long facilityId);
}