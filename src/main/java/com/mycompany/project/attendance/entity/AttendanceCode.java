package com.mycompany.project.attendance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 출결 코드(Entity)
 * - 출석/지각/결석/병가/공가 같은 "코드 마스터" 테이블
 * - Entity는 @Setter 금지(규칙) → 변경은 의미 있는 메서드로만 허용
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙 + 외부에서 무분별한 생성 방지
@Entity
@Table(name = "tbl_attendance_code")
public class AttendanceCode {

    /**
     * PK
     * - DB에서 AUTO_INCREMENT PRIMARY KEY여야 IDENTITY 전략이 정상 동작
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_code_id")
    private Long id;

    /**
     * 시스템 내부 코드값(예: PRESENT, LATE ...)
     */
    @Column(nullable = false, length = 30)
    private String code;

    /**
     * 화면 표시용 이름(예: 출석, 지각 ...)
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 공결/병가 등 참작 여부
     */
    @Column(name = "is_excused", nullable = false)
    private boolean excused;

    /**
     * 사용 여부(soft delete용으로 많이 사용)
     */
    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * 생성/수정 일시
     * - DB default도 있지만, JPA에서 insert/update 시점 값 보장하려고 @PrePersist/@PreUpdate로 한번 더 안전장치
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 생성자
     * - 신규 코드 등록 시 최소 정보만 받도록 설계
     */
    public AttendanceCode(String code, String name, boolean excused) {
        this.code = code;
        this.name = name;
        this.excused = excused;
        this.active = true; // 신규는 기본 활성
    }

    /**
     * INSERT 직전에 자동 세팅
     * - createdAt/updatedAt이 null일 때만 채워서, 테스트/이관 데이터에서 값을 직접 넣는 경우도 허용
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = (this.createdAt == null) ? now : this.createdAt;
        this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;
    }

    /**
     * UPDATE 직전에 수정 시간 갱신
     */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드(Setter 대신) =====

    /** 표시명 변경 */
    public void rename(String name) {
        this.name = name;
    }

    /** 코드값 변경(정책상 허용되는지 팀 룰로 결정) */
    public void changeCode(String code) {
        this.code = code;
    }

    /** 비활성화(삭제 대신 안전하게) */
    public void deactivate() {
        this.active = false;
    }

    /** 재활성화 */
    public void activate() {
        this.active = true;
    }
}
