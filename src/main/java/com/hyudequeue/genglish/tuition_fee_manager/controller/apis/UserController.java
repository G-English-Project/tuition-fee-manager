package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.BulkUserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.ChangePasswordRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentProfileDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.StudentStatusEnum;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.USER_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER_API)
public class UserController {

    private final UserService userService;

    @GetMapping(GET_ALL_STUDENT_ENDPOINT)
    @Operation(
            summary = "Get all students",
            description = "Retrieve a paginated list of all student users, with optional filters."
    )
    public ResponseEntity<?> getAllStudents(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Filter by class ID")
            @RequestParam(required = false) Long classId,

            @Parameter(description = "Filter by class name (contains)")
            @RequestParam(required = false) String className,

            @Parameter(description = "Filter by student status", example = "ACTIVE")
            @RequestParam(required = false) StudentStatusEnum studentStatus,

            @Parameter(description = "Sort by field (createdAt, fullName, classCount)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir,

            @Parameter(description = "Filter students who have NO class (true = no class)")
            @RequestParam(required = false) Boolean noClass
    ) {
        return ApiResp.success(
                userService.GetAllStudent(
                        page, size, classId, className, studentStatus, sortBy, sortDir, noClass
                )
        );
    }


    @Operation(summary = "Create user by role", description = "Create a new user with given role (ADMIN or STUDENT).")
    @PostMapping(CREATE_ENDPOINT)
    public ResponseEntity<?> createUserByRole(
            @RequestParam RoleEnum role,
            @Valid @RequestBody UserCreateRequestDto body
    ) {
        return ApiResp.success(userService.createUserByRole(body, role));
    }

    @Operation(summary = "Edit user profile", description = "Update an existing user profile by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping(EDIT_ENDPOINT)
    public ResponseEntity<?> EditProfile(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Long userId,
            @Parameter(description = "Updated user information", required = true)
            @RequestBody UserEditRequestDto userDto) {
        return ApiResp.success(userService.EditProfile(userDto, userId));
    }

    @Operation(summary = "Delete user", description = "Delete a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping(DELETE_ENDPOINT)
    public ResponseEntity<?> DeleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable Long userId) {
        userService.DeleteStudent(userId);
        return ApiResp.success(null);
    }
    @Operation(
            summary = "Get student profile",
            description = "Retrieve a student profile including user info and list of enrolled classes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Student not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping(PROFILE_ENDPOINT)
    public ResponseEntity<?> getStudentProfile(
            @Parameter(description = "ID of the user to retrieve profile", required = true)
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @Operation(
            summary = "Search students by keyword",
            description = "Search active students by full name, email, or class name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping(SEARCH_ENDPOINT)
    public ResponseEntity<?> searchStudents(
            @Parameter(description = "Search keyword (name, email, or class)", required = true)
            @RequestParam String keyword,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResp.success(userService.searchStudents(keyword, page, size));
    }
    @Operation(summary = "Change user password",
            description = "Change password for a specific user. Must provide oldPassword and newPassword.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Old password is incorrect",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping(CHANGE_PASSWORD_ENDPOINT)
    public ResponseEntity<?> changePassword(
            @Parameter(description = "ID of the user to change password", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequestDto body
    ) {
        userService.changePassword(userId, body.getOldPassword(), body.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get users by role", description = "Paginated users filtered by role (ACTIVE).")
    @GetMapping(GET_ALL_ENDPOINT)
    public ResponseEntity<?> getUsersByRole(
            @RequestParam RoleEnum role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResp.success(userService.getAllByRole(role, page, size));
    }
    

    @Operation(summary = "Create bulk students", description = "Create multiple students at once. Only for admin use.")
    @PostMapping(BULK_CREATE_STUDENTS_ENDPOINT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createBulkStudents(
            @Valid @RequestBody BulkUserCreateRequestDto request) {
        return ApiResp.success(userService.createBulkStudents(request));
    }

    @Operation(summary = "Update student status", description = "Update student status. Only for admin use.")
    @PutMapping("/{userId}/student-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudentStatus(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "New student status", required = true) @RequestParam StudentStatusEnum studentStatus) {
        userService.updateStudentStatus(userId, studentStatus);
        return ApiResp.success("Student status updated successfully");
    }

    @GetMapping(GET_ACTIVE_STUDENTS_BY_TEACHER_ENDPOINT)
    @Operation(summary = "Get active students by teacher", description = "Get all active students enrolled in classes taught by specified teacher")
    public ResponseEntity<?> getActiveStudentsByTeacher(
            @Parameter(description = "Teacher ID", required = true) @RequestParam Long teacherId,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(userService.getActiveStudentsByTeacher(teacherId, page, size));
    }
    @Operation(summary = "Update user role", description = "Update the role of a specific user. ADMIN only.")
    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,

            @Parameter(description = "New role (STUDENT, TEACHER, ADMIN)", required = true)
            @RequestParam RoleEnum role
    ) {
        userService.updateUserRole(userId, role);
        return ApiResp.success("User role updated successfully");
    }

}
