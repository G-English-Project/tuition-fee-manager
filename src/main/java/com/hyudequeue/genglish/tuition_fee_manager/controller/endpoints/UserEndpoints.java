package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class UserEndpoints {
    public static final String GET_ALL_STUDENT_ENDPOINT = "/student";
    public static final String GET_ALL_TA_ENDPOINT = "/ta";
    public static final String GET_ALL_ENDPOINT = "";
    public static final String CREATE_ENDPOINT = "";
    public static final String EDIT_ENDPOINT = "{userId}";
    public static final String DELETE_ENDPOINT = "{userId}";
    public static final String PROFILE_ENDPOINT = "/profile/{userId}";
    public static final String SEARCH_ENDPOINT = "search";
    public static final String CHANGE_PASSWORD_ENDPOINT = "/{userId}/password";
    public static final String BULK_CREATE_STUDENTS_ENDPOINT = "/bulk/students";
    public static final String GET_STUDENTS_BY_TEACHER_ENDPOINT = "/students/by-teacher";
    public static final String GET_ACTIVE_STUDENTS_BY_TEACHER_ENDPOINT = "/students/teacher";


}
