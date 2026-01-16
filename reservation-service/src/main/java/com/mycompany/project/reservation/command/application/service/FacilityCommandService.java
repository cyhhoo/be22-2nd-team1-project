package com.mycompany.project.reservation.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.reservation.command.application.dto.request.FacilityCreateRequest;
import com.mycompany.project.reservation.command.application.dto.request.FacilityUpdateRequest;
import com.mycompany.project.reservation.command.application.dto.response.FacilityCommandResponse;
import com.mycompany.project.reservation.command.domain.aggregate.Facility;
import com.mycompany.project.reservation.command.domain.aggregate.FacilityStatus;
import com.mycompany.project.reservation.command.domain.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class FacilityCommandService {
  private final FacilityRepository facilityRepository;

  /* Register new facility */
  public FacilityCommandResponse create(Long adminId, FacilityCreateRequest req) {

    if (facilityRepository.existsByName(req.getName())) {
      throw new BusinessException(ErrorCode.FACILITY_NAME_DUPLICATED);
    }

    validateTime(req.getOpenTime(), req.getCloseTime());

    Facility facility = Facility.builder()
        .name(req.getName())
        .status(FacilityStatus.AVAILABLE.name())
        .openTime(req.getOpenTime())
        .closeTime(req.getCloseTime())
        .location(req.getLocation())
        .facilityType(req.getFacilityType())
        .adminId(adminId)
        .build();

    return FacilityCommandResponse.from(
        facilityRepository.save(facility));
  }

  /* Update existing facility */
  public FacilityCommandResponse update(Long adminId, Long facilityId, FacilityUpdateRequest req) {

    Facility facility = facilityRepository.findById(facilityId)
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

    if (!facility.getAdminId().equals(adminId)) {
      throw new BusinessException(ErrorCode.FACILITY_UPDATE_FORBIDDEN);
    }

    if (facilityRepository.existsByNameAndFacilityIdNot(req.getName(), facilityId)) {
      throw new BusinessException(ErrorCode.FACILITY_NAME_DUPLICATED);
    }

    FacilityStatus status = parseStatus(req.getStatus());
    validateTime(req.getOpenTime(), req.getCloseTime());

    facility.update(
        req.getName(),
        status,
        req.getOpenTime(),
        req.getCloseTime(),
        req.getLocation(),
        req.getFacilityType());

    return FacilityCommandResponse.from(facility);
  }

  /* Delete facility */
  public void delete(Long adminId, Long facilityId) {

    Facility facility = facilityRepository.findById(facilityId)
        .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

    if (!facility.getAdminId().equals(adminId)) {
      throw new BusinessException(ErrorCode.FACILITY_DELETE_FORBIDDEN);
    }

    facilityRepository.delete(facility);
  }

  private void validateTime(LocalTime open, LocalTime close) {
    if (!open.isBefore(close)) {
      throw new BusinessException(ErrorCode.INVALID_OPERATION_TIME);
    }
  }

  private FacilityStatus parseStatus(String status) {
    try {
      return FacilityStatus.valueOf(status.toUpperCase());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.INVALID_FACILITY_STATUS);
    }
  }
}
