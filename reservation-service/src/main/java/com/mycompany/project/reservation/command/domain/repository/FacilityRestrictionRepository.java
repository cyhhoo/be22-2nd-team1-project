package com.mycompany.project.reservation.command.domain.repository;

import com.mycompany.project.reservation.command.domain.aggregate.FacilityRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface FacilityRestrictionRepository extends JpaRepository<FacilityRestriction, Long> {
    boolean existsByFacilityIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long facilityId, LocalDate date1, LocalDate date2
    );
}
