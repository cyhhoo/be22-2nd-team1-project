package com.mycompany.project.schedule.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.application.dto.SubjectCreateRequest;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubjectCommandServiceTest {

    @InjectMocks
    private SubjectCommandService subjectCommandService;

    @Mock
    private SubjectRepository subjectRepository;

    @Test
    @DisplayName("과목 생성 성공 - 신규 생성")
    void createSubject_Success_New() {
        // given
        SubjectCreateRequest request = new SubjectCreateRequest();
        request.setName("수학");

        given(subjectRepository.findByName("수학")).willReturn(Optional.empty());
        given(subjectRepository.save(any(Subject.class))).willAnswer(invocation -> {
            Subject s = invocation.getArgument(0);
            // mock id
            return Subject.builder().id(1L).name(s.getName()).build();
        });

        // when
        Long id = subjectCommandService.createSubject(request);

        // then
        assertEquals(1L, id);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("과목 생성 성공 - 이미 존재함 (Idempotent)")
    void createSubject_Success_Existing() {
        // given
        SubjectCreateRequest request = new SubjectCreateRequest();
        request.setName("영어");

        Subject existingSubject = Subject.builder().id(100L).name("영어").build();

        given(subjectRepository.findByName("영어")).willReturn(Optional.of(existingSubject));

        // when
        Long id = subjectCommandService.createSubject(request);

        // then
        assertEquals(100L, id);
        // save는 호출되지 않아야 함
        verify(subjectRepository, org.mockito.Mockito.never()).save(any(Subject.class));
    }

    @Test
    @DisplayName("과목 삭제 실패 - 존재하지 않음")
    void deleteSubject_NotFound() {
        // given
        Long subjectId = 999L;
        given(subjectRepository.existsById(subjectId)).willReturn(false);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectCommandService.deleteSubject(subjectId));
        assertEquals(ErrorCode.SUBJECT_NOT_FOUND, ex.getErrorCode());
    }
}
