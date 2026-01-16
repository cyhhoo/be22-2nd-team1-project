package com.mycompany.project.schedule.command.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleType {
    // Semester and Class related
    SEMESTER_START("Semester Start"),
    SEMESTER_END("Semester End"),
    VACATION_START("Vacation Start"),
    VACATION_END("Vacation End"),

    // Evaluation related
    MIDTERM_EXAM("Midterm Exam"),
    FINAL_EXAM("Final Exam"),
    MOCK_EXAM("Mock Exam / Performance Assessment"),

    // Holidays and Anniversaries
    HOLIDAY("Public Holiday"),
    FOUNDATION_DAY("School Foundation Day"),
    DISCRETIONARY_HOLIDAY("Discretionary Holiday"),

    // Academic Administration and Events
    REGISTRATION("Course Registration"),
    ENTRANCE_CEREMONY("Entrance Ceremony"),
    GRADUATION_CEREMONY("Graduation Ceremony"),
    FIELD_TRIP("Field Trip"),
    SCHOOL_FESTIVAL("School Festival / Sports Day"),

    // Consultation and Others
    CONSULTATION("Parent/Student Consultation"),
    SPECIAL_LECTURE("Special Lecture"),
    OTHER("Other");

    private final String description;
}
