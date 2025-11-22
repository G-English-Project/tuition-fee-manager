package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class ClassEndpoints {
    public static final String GET_ALL_CLASSES = "/all";
    public static final String GET_CLASS_BY_ID = "/{classId}";
    public static final String CREATE_CLASS = "/create";
    public static final String EDIT_CLASS = "/edit";
    public static final String REMOVE_CLASS = "/{classId}/remove";
    public static final String MODIFY_CLASS_FEE = "/fee/modify";

    public static final String CURRENT_STUDENTS = "/{classId}/students/current";
    public static final String ALL_STUDENTS = "/{classId}/students/all";
    public static final String ENROLLMENT_BY_STUDENT = "/students/{studentId}/enrollments";
    public static final String ASSIGN_STUDENT = "/{classId}/students/{studentId}/assign";
    public static final String ASSIGN_MULTIPLE_STUDENTS = "/{classId}/students/assign-multiple";
    public static final String REMOVE_STUDENT = "/{classId}/students/{studentId}/remove";
    public static final String NOTE_STUDENT = "/{classId}/students/{studentId}/note";
    public static final String RESTORE_CLASS = "/{classId}/restore";
    public static final String REVENUE_BY_MONTH = "/{classId}/revenue-by-month";
    public static final String GET_CLASSES_BY_TEACHER = "/teacher/{teacherId}";
    public static final String GET_ACTIVE_CLASSES_FOR_LANDING = "/landing/active-classes";
    public static final String ASSIGN_MENTOR = "/{classId}/assign-mentor";
    public static final String REMOVE_MENTOR = "/{classId}/remove-mentor/{mentorId}";

}
