package com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints;

public final class NotificationEndpoints {
    public static final String GET_ALL_BY_USER = "/user/{userId}";
    public static final String GET_ACTIVE_BY_USER = "/active/user/{userId}";
    public static final String MARK_AS_READ = "/read/{notificationId}";
    public static final String MARK_MULTIPLE_AS_READ = "/read";
    public static final String MARK_ALL_AS_READ = "/read-all/{userId}";
    public static final String MARK_AS_DELETE = "/delete/{notificationId}";
    public static final String MARK_ALL_AS_DELETE = "/delete-all/{userId}";
    public static final String COUNT_UNREAD = "/unread-count/{userId}";
}
