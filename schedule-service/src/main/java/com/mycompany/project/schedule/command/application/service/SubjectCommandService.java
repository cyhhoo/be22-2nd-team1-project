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
     * Register subject
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
                    return subjectRepository.save(java.util.Objects.requireNonNull(subject)).getId();
                });
    }

    /**
     * Delete subject
     */
    @Transactional
    public void deleteSubject(Long id) {
        java.util.Objects.requireNonNull(id, "Subject ID must not be null");
        if (!subjectRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        subjectRepository.deleteById(id);
    }
}
