package com.mycompany.project.attendance.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 異쒓껐 肄붾뱶(Entity)
 * - 異쒖꽍/吏媛?寃곗꽍/蹂묎?/怨듦? 媛숈? "肄붾뱶 留덉뒪?? ?뚯씠釉?
 * - Entity??@Setter 湲덉?(洹쒖튃) ??蹂寃쎌? ?섎? ?덈뒗 硫붿꽌?쒕줈留??덉슜
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA ?ㅽ럺 + ?몃??먯꽌 臾대텇蹂꾪븳 ?앹꽦 諛⑹?
@Entity
@AllArgsConstructor
@Builder
@Table(name = "tbl_attendance_code")
public class AttendanceCode {

    /**
     * PK
     * - DB?먯꽌 AUTO_INCREMENT PRIMARY KEY?ъ빞 IDENTITY ?꾨왂???뺤긽 ?숈옉
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_code_id")
    private Long id;

    /**
     * ?쒖뒪???대? 肄붾뱶媛??? PRESENT, LATE ...)
     */
    @Column(nullable = false, length = 30)
    private String code;

    /**
     * ?붾㈃ ?쒖떆???대쫫(?? 異쒖꽍, 吏媛?...)
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 怨듦껐/蹂묎? ??李몄옉 ?щ?
     */
    @Column(name = "is_excused", nullable = false)
    private boolean excused;

    /**
     * ?ъ슜 ?щ?(soft delete?⑹쑝濡?留롮씠 ?ъ슜)
     */
    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * ?앹꽦/?섏젙 ?쇱떆
     * - DB default???덉?留? JPA?먯꽌 insert/update ?쒖젏 媛?蹂댁옣?섎젮怨?@PrePersist/@PreUpdate濡??쒕쾲 ???덉쟾?μ튂
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * ?앹꽦??
     * - ?좉퇋 肄붾뱶 ?깅줉 ??理쒖냼 ?뺣낫留?諛쏅룄濡??ㅺ퀎
     */
    public AttendanceCode(String code, String name, boolean excused) {
        this.code = code;
        this.name = name;
        this.excused = excused;
        this.active = true; // ?좉퇋??湲곕낯 ?쒖꽦
    }

    /**
     * INSERT 吏곸쟾???먮룞 ?명똿
     * - createdAt/updatedAt??null???뚮쭔 梨꾩썙?? ?뚯뒪???닿? ?곗씠?곗뿉??媛믪쓣 吏곸젒 ?ｋ뒗 寃쎌슦???덉슜
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = (this.createdAt == null) ? now : this.createdAt;
        this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;
    }

    /**
     * UPDATE 吏곸쟾???섏젙 ?쒓컙 媛깆떊
     */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 鍮꾩쫰?덉뒪 硫붿꽌??Setter ??? =====

    /** ?쒖떆紐?蹂寃?*/
    public void rename(String name) {
        this.name = name;
    }

    /** 肄붾뱶媛?蹂寃??뺤콉???덉슜?섎뒗吏 ? 猷곕줈 寃곗젙) */
    public void changeCode(String code) {
        this.code = code;
    }

    /** 鍮꾪솢?깊솕(??젣 ????덉쟾?섍쾶) */
    public void deactivate() {
        this.active = false;
    }

    /** ?ы솢?깊솕 */
    public void activate() {
        this.active = true;
    }
}
