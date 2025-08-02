package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.StudentProfileDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.USER_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER_API)
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all students", description = "Retrieve a paginated list of all student users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students retrieved successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping(GET_ALL_ENDPOINT)
    public ResponseEntity<?> getAllStudents(
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(userService.GetAllStudent(page, size));
    }

    @Operation(summary = "Create new student", description = "Create a new user with student role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student created successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping(CREATE_ENDPOINT)
    public ResponseEntity<?> CreateStudent(
            @Parameter(description = "User creation request body", required = true)
            @RequestBody UserCreateRequestDto userCreate) {
        return ApiResp.success(userService.CreateStudent(userCreate));
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

}
