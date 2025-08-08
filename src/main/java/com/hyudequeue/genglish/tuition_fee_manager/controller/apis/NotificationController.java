package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.NOTIFICATION_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(NOTIFICATION_API)
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications by user", description = "Returns paginated list of notifications by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    @GetMapping(GET_ALL_BY_USER)
    public ResponseEntity<?> getNotificationsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {
        return ApiResp.success(notificationService.getNotifications(userId, PageRequest.of(pageNumber, pageSize)));
    }

    @Operation(summary = "Mark one notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read")
    })
    @PutMapping(MARK_AS_READ)
    public ResponseEntity<?> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResp.success("Marked as read.");
    }

    @Operation(summary = "Mark multiple notifications as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications marked as read")
    })
    @PutMapping(MARK_MULTIPLE_AS_READ)
    public ResponseEntity<?> markMultipleAsRead(
            @Parameter(description = "List of notification IDs") @RequestBody List<Long> notificationIds) {
        notificationService.markAsRead(notificationIds);
        return ApiResp.success("Marked multiple as read.");
    }

    @Operation(summary = "Mark all notifications as read for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All notifications marked as read")
    })
    @PutMapping(MARK_ALL_AS_READ)
    public ResponseEntity<?> markAllAsRead(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResp.success("Marked all as read.");
    }

    @Operation(summary = "Count unread notifications for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unread count retrieved")
    })
    @GetMapping(COUNT_UNREAD)
    public ResponseEntity<?> countUnread(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        return ApiResp.success(notificationService.countUnreadNotifications(userId));
    }
}
