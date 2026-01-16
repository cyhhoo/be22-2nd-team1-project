package com.mycompany.project.schedule.query.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import com.mycompany.project.schedule.query.dto.SubjectResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SubjectQueryServiceTest {

    @InjectMocks
    private SubjectQueryService subjectQueryService;

    @Mock
    private SubjectRepository subjectRepository;

    @Test
    @DisplayName("?꾩껜 怨쇰ぉ 議고쉶 ?깃났")
    void getAllSubjects_Success() {
        // given
        List<Subject> mockSubjects = Arrays.asList(
                Subject.builder().id(1L).name("?섑븰").build(),
                Subject.builder().id(2L).name("?곸뼱").build());
        given(subjectRepository.findAll()).willReturn(mockSubjects);

        // when
        List<SubjectResponse> responses = subjectQueryService.getAllSubjects();

        // then
        assertEquals(2, responses.size());
        assertEquals("?섑븰", responses.get(0).getName());
        assertEquals("?곸뼱", responses.get(1).getName());
    }

    @Test
    @DisplayName("?⑥씪 怨쇰ぉ 議고쉶 ?깃났")
    void getSubjectById_Success() {
        // given
        Long subjectId = 1L;
        Subject mockSubject = Subject.builder().id(subjectId).name("?섑븰").build();
        given(subjectRepository.findById(subjectId)).willReturn(Optional.of(mockSubject));

        // when
        SubjectResponse response = subjectQueryService.getSubjectById(subjectId);

        // then
        assertNotNull(response);
        assertEquals(subjectId, response.getId());
        assertEquals("?섑븰", response.getName());
    }

    @Test
    @DisplayName("?⑥씪 怨쇰ぉ 議고쉶 ?ㅽ뙣 - 議댁옱?섏? ?딆쓬")
    void getSubjectById_NotFound() {
        // given
        Long subjectId = 99L;
        given(subjectRepository.findById(subjectId)).willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> subjectQueryService.getSubjectById(subjectId));
        assertEquals(ErrorCode.SUBJECT_NOT_FOUND, ex.getErrorCode());
    }
}
