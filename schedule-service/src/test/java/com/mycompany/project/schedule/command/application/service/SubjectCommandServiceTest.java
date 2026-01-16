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
    @DisplayName("Subject creation success - New")
    void createSubject_Success_New() {
        // given
        SubjectCreateRequest request = new SubjectCreateRequest();
        request.setName("Math");

        given(subjectRepository.findByName("Math")).willReturn(Optional.empty());
        given(subjectRepository.save(java.util.Objects.requireNonNull(any(Subject.class)))).willAnswer(invocation -> {
            Subject s = invocation.getArgument(0);
            // mock id
            return Subject.builder().id(1L).name(s.getName()).build();
        });

        // when
        Long id = subjectCommandService.createSubject(request);

        // then
        assertEquals(1L, id);
        verify(subjectRepository).save(java.util.Objects.requireNonNull(any(Subject.class)));
    }

    @Test
    @DisplayName("Subject creation success - Already exists (Idempotent)")
    void createSubject_Success_Existing() {
        // given
        SubjectCreateRequest request = new SubjectCreateRequest();
        request.setName("English");

        Subject existingSubject = Subject.builder().id(100L).name("English").build();

        given(subjectRepository.findByName("English")).willReturn(Optional.of(existingSubject));

        // when
        Long id = subjectCommandService.createSubject(request);

        // then
        assertEquals(100L, id);
        // save should not be called
        verify(subjectRepository, org.mockito.Mockito.never())
                .save(java.util.Objects.requireNonNull(any(Subject.class)));
    }

    @Test
    @DisplayName("Subject deletion fail - Not found")
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
