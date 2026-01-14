package com.mycompany.project.schedule.command.application.service;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.application.dto.SubjectCreateRequest;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubjectCommandService {

    private final SubjectRepository subjectRepository;

    /**
     * 과목 등록
     */
    @Transactional
    public Long createSubject(SubjectCreateRequest request) {
        // If subject already exists, return its ID (idempotent)
        return subjectRepository.findByName(request.getName())
                .map(Subject::getId)
                .orElseGet(() -> {
                    Subject subject = Subject.builder()
                            .name(request.getName())
                            .build();
                    return subjectRepository.save(subject).getId();
                });
    }

    /**
     * 과목 삭제
     */
    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        subjectRepository.deleteById(id);
    }
}
