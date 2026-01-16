package com.mycompany.project.schedule.query.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import com.mycompany.project.schedule.query.dto.InternalAcademicYearResponse;
import com.mycompany.project.schedule.query.dto.ScheduleDTO;
import com.mycompany.project.schedule.query.mapper.ScheduleMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {

    @InjectMocks
    private ScheduleQueryService scheduleQueryService;

    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Test
    @DisplayName("?붽컙 ?쇱젙 議고쉶 ?깃났")
    void getMonthlySchedules_Success() {
        // given
        int year = 2025;
        int month = 3;
        List<ScheduleDTO> mockSchedules = Arrays.asList(
                new ScheduleDTO(), new ScheduleDTO());
        given(scheduleMapper.selectMonthlySchedules(year, month)).willReturn(mockSchedules);

        // when
        List<ScheduleDTO> result = scheduleQueryService.getMonthlySchedules(year, month);

        // then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("二쇨컙 ?쇱젙 議고쉶 ?깃났")
    void getWeeklySchedules_Success() {
        // given
        LocalDate start = LocalDate.of(2025, 3, 1);
        LocalDate end = LocalDate.of(2025, 3, 7);
        List<ScheduleDTO> mockSchedules = Arrays.asList(
                new ScheduleDTO());
        given(scheduleMapper.selectWeeklySchedules(start, end)).willReturn(mockSchedules);

        // when
        List<ScheduleDTO> result = scheduleQueryService.getWeeklySchedules(start, end);

        // then
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("?대? ?숇뀈???뺣낫 議고쉶 ?깃났")
    void getInternalAcademicYear_Success() {
        // given
        Long id = 1L;
        AcademicYear mockYear = AcademicYear.builder()
                .academicYearId(id)
                .year(2025)
                .semester(1)
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 6, 20))
                .build();
        given(academicYearRepository.findById(id)).willReturn(Optional.of(mockYear));

        // when
        InternalAcademicYearResponse response = scheduleQueryService.getInternalAcademicYear(id);

        // then
        assertNotNull(response);
        assertEquals(id, response.getAcademicYearId());
        assertEquals("2025-1", response.getName());
    }

    @Test
    @DisplayName("?대? ?숇뀈???뺣낫 議고쉶 ?ㅽ뙣 - ?뺣낫 ?놁쓬")
    void getInternalAcademicYear_NotFound() {
        // given
        Long id = 99L;
        given(academicYearRepository.findById(id)).willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> scheduleQueryService.getInternalAcademicYear(id));
        assertEquals(ErrorCode.ACADEMIC_TERM_INFO_MISSING, ex.getErrorCode());
    }
}
