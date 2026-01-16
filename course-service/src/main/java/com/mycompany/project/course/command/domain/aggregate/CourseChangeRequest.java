package com.mycompany.project.course.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_course_change_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CourseChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", length = 20)
    private RequestStatus requestStatus = RequestStatus.PENDING; // 湲곕낯媛?PENDING

    @Column(name = "reason", length = 500)
    private String reason; // 蹂寃??붿껌 ?ъ쑀

    @Column(name = "admin_comment", length = 500)
    private String adminComment; // 愿由ъ옄 肄붾찘??(諛섎젮 ????

    // 蹂寃쏀븷 ?곗씠??(JSON 吏곷젹?????媛?뺤쓣 ?꾪빐 String?쇰줈 泥섎━?섍굅?? ?듭떖 ?꾨뱶留?而щ읆?쇰줈)
    // ?ш린???듭떖 蹂寃?媛???꾨뱶?ㅼ쓣 留ㅽ븨
    @Column(name = "target_max_capacity")
    private Integer targetMaxCapacity;

    @Column(name = "target_tuition")
    private Integer targetTuition;

    @Column(name = "target_teacher_detail_id")
    private Long targetTeacherDetailId;

    // 留뚯빟 ?쒓컙??蹂寃??깆씠 ?ы븿?쒕떎硫?蹂듭옟?댁?誘濡?
    // ?ш린?쒕뒗 媛꾨떒???뺤썝, ?섍컯猷??뺣룄留?蹂寃??붿껌?쒕떎怨?媛?뺥븯嫄곕굹,
    // ?꾩껜 ?섏젙 ?곗씠?곕? JSON String?쇰줈 ??ν븯??諛⑹떇???뺤옣?깆씠 醫뗭쓬.
    // ?섏?留?DB ?명솚??MariaDB JSON) 諛?援ы쁽 ?⑥닚?붾? ?꾪빐 ?듭떖 ?꾨뱶 紐?媛쒕쭔 ?덉떆濡???

    // 媛뺤쥖紐낆씠???좏삎? ???덈컮?뚮?濡? ?뺤썝/?섍컯猷?蹂寃??붿껌 ?꾩＜濡?援ы쁽

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    @Builder
    public CourseChangeRequest(Course course, String reason, Integer targetMaxCapacity, Integer targetTuition,
            Long targetTeacherDetailId) {
        this.course = course;
        this.reason = reason;
        this.targetMaxCapacity = targetMaxCapacity;
        this.targetTuition = targetTuition;
        this.targetTeacherDetailId = targetTeacherDetailId;
    }

    public void approve() {
        this.requestStatus = RequestStatus.APPROVED;
    }

    public void reject(String adminComment) {
        this.requestStatus = RequestStatus.REJECTED;
        this.adminComment = adminComment;
    }
}
