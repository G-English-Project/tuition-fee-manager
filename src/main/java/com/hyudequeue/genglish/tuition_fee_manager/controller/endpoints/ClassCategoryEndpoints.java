package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public class ClassCategoryEndpoints {
    private static final String API_BASE = "/api/v1/class-categories";

    public static final String GET_ALL = API_BASE;
    public static final String GET_BY_ID = API_BASE + "/{id}";
    public static final String CREATE = API_BASE;
    public static final String UPDATE = API_BASE + "/{id}";
    public static final String DELETE = API_BASE + "/{id}";
}

