package com.mycompany.project.schedule.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.application.dto.AcademicYearDTO;
import com.mycompany.project.schedule.command.application.dto.ScheduleCreateRequest;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicSchedule;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.aggregate.ScheduleType;
import com.mycompany.project.schedule.command.domain.repository.AcademicScheduleRepository;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("학년도 생성 성공 - 신규")
    void createAcademicYear_Success_New() {
        // given
        AcademicYearDTO request = new AcademicYearDTO();
        request.setYear(2025);
        request.setSemester(1);

        given(academicYearRepository.findByYearAndSemester(2025, 1)).willReturn(Optional.empty());
        given(academicYearRepository.save(any(AcademicYear.class))).willAnswer(inv -> {
            AcademicYear ay = inv.getArgument(0);
            return AcademicYear.builder().academicYearId(1L).year(ay.getYear()).build();
        });

        // when
        Long id = scheduleCommandService.createAcademicYear(request);

        // then
        assertEquals(1L, id);
        verify(academicYearRepository).save(any(AcademicYear.class));
    }

    @Test
    @DisplayName("상세 일정 등록 성공")
    void createSchedule_Success() {
        // given
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setAcademicYearId(1L);
        request.setScheduleDate(LocalDate.now());
        request.setScheduleType(ScheduleType.OTHER);
        request.setContent("중간고사");

        AcademicYear mockYear = AcademicYear.builder().academicYearId(1L).build();
        given(academicYearRepository.findById(1L)).willReturn(Optional.of(mockYear));
        given(academicScheduleRepository.save(any(AcademicSchedule.class))).willAnswer(inv -> {
            AcademicSchedule s = inv.getArgument(0);
            return AcademicSchedule.builder().scheduleId(10L).build();
        });

        // when
        Long id = scheduleCommandService.createSchedule(request);

        // then
        assertEquals(10L, id);
    }

    @Test
    @DisplayName("상세 일정 등록 실패 - 학년도 없음")
    void createSchedule_AcademicYearNotFound() {
        // given
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setAcademicYearId(99L);

        given(academicYearRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> scheduleCommandService.createSchedule(request));
        assertEquals(ErrorCode.SCHEDULE_ACADEMIC_YEAR_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("일정 수정 성공")
    void updateSchedule_Success() {
        // given
        Long scheduleId = 10L;
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setContent("수정된 내용");

        AcademicSchedule mockSchedule = AcademicSchedule.builder().scheduleId(10L).content("기존 내용").build();

        given(academicScheduleRepository.findById(scheduleId)).willReturn(Optional.of(mockSchedule));

        // when
        scheduleCommandService.updateSchedule(scheduleId, request);

        // then
        assertEquals("수정된 내용", mockSchedule.getContent());
    }

    @Test
    @DisplayName("일정 삭제 성공 (Soft Delete)")
    void deleteSchedule_Success() {
        // given
        Long scheduleId = 10L;
        AcademicSchedule mockSchedule = AcademicSchedule.builder().scheduleId(10L).isDeleted(false).build();

        given(academicScheduleRepository.findById(scheduleId)).willReturn(Optional.of(mockSchedule));

        // when
        scheduleCommandService.deleteSchedule(scheduleId);

        // then
        assertTrue(mockSchedule.isDeleted());
    }
}
