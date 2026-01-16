package com.mycompany.project.attendance.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.attendance.command.domain.aggregate.Attendance;
import com.mycompany.project.attendance.command.domain.aggregate.enums.AttendanceState;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 異쒓껐(Attendance) JPA Repository
 * - 異쒓껐 ?앹꽦/????뺤젙/留덇컧 媛숈? "?곌린" ?묒뾽?먯꽌 ?ъ슜
 * - enrollment_id + ?좎쭨 + 援먯떆 ?⑥쐞濡?異쒓껐??李얜뒗 荑쇰━媛 留롮븘??硫붿꽌?쒕줈 戮묒븘???곹깭
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

  /**
   * ?뱀젙 ?섍컯?좎껌(enrollment) + ?좎쭨 + 援먯떆???대떦?섎뒗 異쒓껐 1嫄?議고쉶
   * - 異쒖꽍遺 ?앹꽦 ??"?대? 留뚮뱾?댁죱?붿?" 以묐났 泥댄겕??
   */
  Optional<Attendance> findByEnrollmentIdAndClassDateAndPeriod(Long enrollmentId, LocalDate classDate, byte period);

  /**
   * ?щ윭 ?섍컯?좎껌(enrollmentIds) + ?좎쭨 + 援먯떆???대떦?섎뒗 異쒓껐 紐⑸줉 議고쉶
   * - ?뺤젙/?????????숈깮?ㅼ쓽 異쒓껐????踰덉뿉 媛?몄삱 ???ъ슜
   */
  List<Attendance> findByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds,
                                                             LocalDate classDate,
                                                             byte period);

  /**
   * ?щ윭 ?섍컯?좎껌(enrollmentIds) + ?좎쭨 + 援먯떆???대떦?섎뒗 異쒓껐 媛쒖닔 議고쉶
   * - "誘몄엯??異쒓껐???덈뒗吏" 泥댄겕?????ъ슜 (count < enrollmentIds.size()硫??꾨씫)
   */
  long countByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds,
                                                  LocalDate classDate,
                                                  byte period);

  /**
   * ?щ윭 ?섍컯?좎껌(enrollmentIds) + ?좎쭨 踰붿쐞(from~to)???대떦?섎뒗 異쒓껐 紐⑸줉 議고쉶
   * - 留덇컧 泥섎━ 踰붿쐞(???숆린) 異쒓껐??紐⑥븘??媛?몄삱 ???ъ슜
   */
  List<Attendance> findByEnrollmentIdInAndClassDateBetween(Collection<Long> enrollmentIds,
                                                           LocalDate fromDate,
                                                           LocalDate toDate);

  /**
   * ?щ윭 ?섍컯?좎껌(enrollmentIds) + ?좎쭨 踰붿쐞(from~to) + ?곹깭(state)???대떦?섎뒗 異쒓껐 紐⑸줉 議고쉶
   * - ?? CONFIRMED留?紐⑥븘??留덇컧 泥섎━?섍굅?? ?곹깭蹂?吏묎퀎/寃利앺븷 ???ъ슜
   */
  List<Attendance> findByEnrollmentIdInAndClassDateBetweenAndState(Collection<Long> enrollmentIds,
                                                                   LocalDate fromDate,
                                                                   LocalDate toDate,
                                                                   AttendanceState state);
}