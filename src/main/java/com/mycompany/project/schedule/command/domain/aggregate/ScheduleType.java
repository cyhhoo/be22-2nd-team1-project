package com.mycompany.project.schedule.command.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleType {
    // 학기 및 수업 관련
    SEMESTER_START("개강"),
    SEMESTER_END("종강"),
    VACATION_START("방학 시작"),
    VACATION_END("방학 종료"),
    
    // 평가 관련
    MIDTERM_EXAM("중간고사"),
    FINAL_EXAM("기말고사"),
    MOCK_EXAM("모의고사/수행평가"),
    
    // 휴무 및 기념일
    HOLIDAY("공휴일"),
    FOUNDATION_DAY("개교기념일"),
    DISCRETIONARY_HOLIDAY("재량휴업일"),
    
    // 학사 행정 및 행사
    REGISTRATION("수강신청/등록"),
    ENTRANCE_CEREMONY("입학식"),
    GRADUATION_CEREMONY("졸업식"),
    FIELD_TRIP("현장학습/수학여행"),
    SCHOOL_FESTIVAL("축제/체육대회"),
    
    // 상담 및 기타
    CONSULTATION("학부모/학생 상담"),
    SPECIAL_LECTURE("특강/설명회"),
    OTHER("기타");

    private final String description;
}
