package com.mycompany.project.schedule.service;
import com.mycompany.project.schedule.query.dto.ScheduleDTO;
import com.mycompany.project.schedule.query.mapper.ScheduleMapper;
import com.mycompany.project.schedule.query.service.ScheduleQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class ScheduleQueryServiceTest {
  @InjectMocks
  private ScheduleQueryService scheduleQueryService;
  @Mock
  private ScheduleMapper scheduleMapper;
  private int year;
  private int month;
  private List<ScheduleDTO> mockList;
  @BeforeEach
  void setUp() {
    year = 2024;
    month = 3;
    ScheduleDTO dto1 = ScheduleDTO.builder()
        .scheduleId(1L)
        .scheduleDate(LocalDate.of(2024, 3, 2))
        .content("개강")
        .build();

    mockList = List.of(dto1);
  }
  @Test
  @DisplayName("월별 학사 일정 조회 테스트")
  void getMonthlySchedules_Success() {
    // given
    given(scheduleMapper.selectMonthlySchedules(year, month))
        .willReturn(mockList);
    // when
    List<ScheduleDTO> result = scheduleQueryService.getMonthlySchedules(year, month);
    // then
    verify(scheduleMapper).selectMonthlySchedules(year, month);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals("개강", result.get(0).getContent());
  }
}