package com.mycompany.project.schedule.query.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.schedule.query.dto.SubjectResponse;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectQueryService {

    private final SubjectRepository subjectRepository;

    /**
     * 전체 과목 목록 조회
     */
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(s -> new SubjectResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 단일 과목 조회
     */
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBJECT_NOT_FOUND));
        return new SubjectResponse(subject.getId(), subject.getName());
    }
}
