    package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

    import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Notification.response.NotificationResponseDto;
    import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
    import com.hyudequeue.genglish.tuition_fee_manager.service.services.NotificationService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.NotificationEndpoints.*;
    import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.NOTIFICATION_API;

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
        public ResponseEntity<ApiResp<Page<NotificationResponseDto>>> getNotificationsByUser(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
                @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {
            return ApiResp.success(notificationService.getNotifications(userId, PageRequest.of(pageNumber, pageSize)));
        }

        @Operation(summary = "Get active notifications by user for bell icon", description = "Returns paginated list of active (non-deleted) notifications by user ID.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Active notifications retrieved successfully")
        })
        @GetMapping(GET_ACTIVE_BY_USER)
        public ResponseEntity<ApiResp<Page<NotificationResponseDto>>> getActiveNotificationsByUser(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int pageNumber,
                @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int pageSize) {
            return ApiResp.success(notificationService.getActiveNotifications(userId, PageRequest.of(pageNumber, pageSize)));
        }

        @Operation(summary = "Mark one notification as read")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Notification marked as read")
        })
        @PutMapping(MARK_AS_READ)
        public ResponseEntity<ApiResp<String>> markAsRead(
                @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
            notificationService.markAsRead(notificationId);
            return ApiResp.success("Marked as read.");
        }

        @Operation(summary = "Mark multiple notifications as read")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Notifications marked as read")
        })
        @PutMapping(MARK_MULTIPLE_AS_READ)
        public ResponseEntity<ApiResp<String>> markMultipleAsRead(
                @Parameter(description = "List of notification IDs") @RequestBody List<Long> notificationIds) {
            notificationService.markAsRead(notificationIds);
            return ApiResp.success("Marked multiple as read.");
        }

        @Operation(summary = "Mark all notifications as read for a user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "All notifications marked as read")
        })
        @PutMapping(MARK_ALL_AS_READ)
        public ResponseEntity<ApiResp<String>> markAllAsRead(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            notificationService.markAllAsRead(userId);
            return ApiResp.success("Marked all as read.");
        }

        @Operation(summary = "Mark one notification as delete")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Notification marked as delete")
        })
        @DeleteMapping(MARK_AS_DELETE)
        public ResponseEntity<ApiResp<String>> markAsDelete(
                @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
            notificationService.markAsDelete(notificationId);
            return ApiResp.success("Marked as deleted.");
        }

        @Operation(summary = "Mark all notifications as delete for a user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "All notifications marked as delete")
        })
        @DeleteMapping(MARK_ALL_AS_DELETE)
        public ResponseEntity<ApiResp<String>> markAllAsDelete(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            notificationService.markAllAsDelete(userId);
            return ApiResp.success("Marked all as deleted.");
        }


        @Operation(summary = "Count unread notifications for a user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Unread count retrieved")
        })
        @GetMapping(COUNT_UNREAD)
        public ResponseEntity<ApiResp<Long>> countUnread(
                @Parameter(description = "User ID") @PathVariable Long userId) {
            return ApiResp.success(notificationService.countUnreadNotifications(userId));
        }
        @Operation(summary = "Create a notification for a user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Notification created successfully")
        })
        @PostMapping("/create/{userId}")
        public ResponseEntity<ApiResp<NotificationResponseDto>> createNotification(
                @Parameter(description = "User ID") @PathVariable Long userId,
                @Parameter(description = "Notification subject") @RequestParam String subject,
                @Parameter(description = "Notification body") @RequestParam String body) {

            NotificationResponseDto created = notificationService.createNotification(userId, subject, body);
            return ApiResp.success(created);
        }

    }
