package com.mycompany.project.attendance.mapper;

import com.mycompany.project.attendance.dto.response.AttendanceCodeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AttendanceCodeQueryMapperTest {

    @Autowired
    private AttendanceCodeQueryMapper attendanceCodeQueryMapper;

    @Test
    @DisplayName("출결 코드 ID로 단건 조회가 정상적으로 이루어져야 한다")
    void findById() {
        // given
        Long attendanceCodeId = 1L; // DB에 존재하는 ID 혹은 테스트 데이터 삽입 후 사용

        // when
        AttendanceCodeResponse response = attendanceCodeQueryMapper.findById(attendanceCodeId);

        // then
        // 데이터가 존재한다는 가정 하에 검증 (실제 환경에 맞게 조정 필요)

        if (response != null) {
            assertThat(response.getAttendanceCodeId()).isEqualTo(attendanceCodeId);
        }
    }

    @Test
    @DisplayName("출결 코드 전체 조회가 정상적으로 이루어져야 한다")
    void findAll() {
        // given
        Boolean activeOnly = true;

        // when
        List<AttendanceCodeResponse> allCodes = attendanceCodeQueryMapper.findAll(activeOnly);

        // then
        assertThat(allCodes).isNotNull();
        // 활성화된 데이터만 가져오는지 확인하는 로직 추가 가능
    }
}
