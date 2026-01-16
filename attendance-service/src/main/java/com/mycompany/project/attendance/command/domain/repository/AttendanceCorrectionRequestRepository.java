package com.mycompany.project.attendance.command.domain.repository;

import com.mycompany.project.attendance.command.domain.aggregate.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.command.domain.aggregate.enums.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ?뺤젙?붿껌(AttendanceCorrectionRequest) JPA Repository
 * - ?뺤젙?붿껌 ?앹꽦/?뱀씤/諛섎젮 媛숈? "?곌린" ?묒뾽?먯꽌 ?ъ슜
 */
public interface AttendanceCorrectionRequestRepository extends JpaRepository<AttendanceCorrectionRequest, Long> {

    /**
     * ?숈씪 異쒓껐(attendance_id)??????뱀젙 ?곹깭(PENDING ?????뺤젙?붿껌???대? ?덈뒗吏 ?뺤씤
     * - 以묐났 ?뺤젙?붿껌 ?앹꽦 諛⑹???
     */
    boolean existsByAttendanceIdAndStatus(Long attendanceId, CorrectionStatus status);
}