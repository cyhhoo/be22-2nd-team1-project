package com.mycompany.project.schedule.service;

import com.mycompany.project.schedule.command.application.dto.ScheduleCreateRequest;
import com.mycompany.project.schedule.command.application.service.ScheduleCommandService;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicSchedule;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.aggregate.ScheduleType;
import com.mycompany.project.schedule.command.domain.repository.AcademicScheduleRepository;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduleCommandServiceTest {

  @InjectMocks
  private ScheduleCommandService scheduleCommandService;
  @Mock
  private AcademicScheduleRepository academicScheduleRepository;
  @Mock
  private AcademicYearRepository academicYearRepository;

  private Long academicYearId;
  private AcademicYear mockYear;
  private ScheduleCreateRequest request;

  @BeforeEach
  void setUp() {
    academicYearId = 1L;
    mockYear = AcademicYear.builder()
        .year(2024)
        .semester(1)
        .build();

    request = new ScheduleCreateRequest();
    request.setAcademicYearId(academicYearId);
    request.setScheduleDate(LocalDate.of(2024, 3, 2));
    request.setScheduleType(ScheduleType.SEMESTER_START);
    request.setContent("1학기 개강");
    request.setTargetGrade("ALL");
  }
  @Test
  @DisplayName("학사 일정 등록 성공 테스트")
  void createSchedule_Success() {

    // given
    given(academicYearRepository.findById(academicYearId))
        .willReturn(Optional.of(mockYear));
    given(academicScheduleRepository.save(any(AcademicSchedule.class)))
        .willAnswer(invocation -> {
          AcademicSchedule saved = invocation.getArgument(0);
          return AcademicSchedule.builder()
              .academicYear(saved.getAcademicYear())
              .scheduleDate(saved.getScheduleDate())
              .scheduleType(saved.getScheduleType())
              .content(saved.getContent())
              .targetGrade(saved.getTargetGrade())
              .build();
        });

    // when
    scheduleCommandService.createSchedule(request);

    // then
    ArgumentCaptor<AcademicSchedule> captor = ArgumentCaptor.forClass(AcademicSchedule.class);
    verify(academicScheduleRepository).save(captor.capture());
    AcademicSchedule capturedSchedule = captor.getValue();

    assertNotNull(capturedSchedule);
    assertEquals(request.getScheduleDate(), capturedSchedule.getScheduleDate());
    assertEquals(request.getScheduleType(), capturedSchedule.getScheduleType());
    assertEquals(request.getContent(), capturedSchedule.getContent());
    assertEquals(request.getTargetGrade(), capturedSchedule.getTargetGrade());
    assertEquals(mockYear, capturedSchedule.getAcademicYear());
  }
}