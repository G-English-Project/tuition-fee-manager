package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.request.TeacherRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Teacher.response.TeacherResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.TeacherEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.TEACHER_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(TEACHER_API)
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "Create a new teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Teacher created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Teacher already exists for this user")
    })
    @PostMapping(CREATE)
    public ResponseEntity<ApiResp<TeacherResponseDto>> createTeacher(
            @RequestBody TeacherRequestDto request) {
        try {
            TeacherResponseDto teacher = teacherService.createTeacher(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(true)
                            .data(teacher)
                            .build());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }

    @Operation(summary = "Get teacher by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @GetMapping(GET_BY_ID)
    public ResponseEntity<ApiResp<TeacherResponseDto>> getTeacherById(
            @Parameter(description = "Teacher ID") @PathVariable Long id) {
        try {
            TeacherResponseDto teacher = teacherService.getTeacherById(id);
            return ApiResp.success(teacher);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }

    @Operation(summary = "Get teacher by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @GetMapping(GET_BY_USER_ID)
    public ResponseEntity<ApiResp<TeacherResponseDto>> getTeacherByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        try {
            TeacherResponseDto teacher = teacherService.getTeacherByUserId(userId);
            return ApiResp.success(teacher);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }

    @Operation(summary = "Get all teachers with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully")
    })
    @GetMapping(GET_ALL)
    public ResponseEntity<ApiResp<Page<TeacherResponseDto>>> getAllTeachers(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Page<TeacherResponseDto> teachers = teacherService.getAllTeachers(PageRequest.of(page, size));
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Update teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher updated successfully"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @PutMapping(UPDATE)
    public ResponseEntity<ApiResp<TeacherResponseDto>> updateTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long id,
            @RequestBody TeacherRequestDto request) {
        try {
            TeacherResponseDto teacher = teacherService.updateTeacher(id, request);
            return ApiResp.success(teacher);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }

    @Operation(summary = "Delete teacher")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teacher deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @DeleteMapping(DELETE)
    public ResponseEntity<ApiResp<String>> deleteTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long id) {
        try {
            teacherService.deleteTeacher(id);
            return ApiResp.success("Teacher deleted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<String>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }

    @Operation(summary = "Search teachers by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers found")
    })
    @GetMapping(SEARCH_BY_NAME)
    public ResponseEntity<ApiResp<Page<TeacherResponseDto>>> searchTeachersByName(
            @Parameter(description = "Teacher name") @RequestParam String name,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Page<TeacherResponseDto> teachers = teacherService.searchTeachersByName(name, PageRequest.of(page, size));
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Get teachers by specialty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers found")
    })
    @GetMapping(GET_BY_SPECIALTY)
    public ResponseEntity<ApiResp<List<TeacherResponseDto>>> getTeachersBySpecialty(
            @Parameter(description = "Specialty") @PathVariable String specialty) {
        List<TeacherResponseDto> teachers = teacherService.getTeachersBySpecialty(specialty);
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Get teachers by language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers found")
    })
    @GetMapping(GET_BY_LANGUAGE)
    public ResponseEntity<ApiResp<List<TeacherResponseDto>>> getTeachersByLanguage(
            @Parameter(description = "Language") @PathVariable String language) {
        List<TeacherResponseDto> teachers = teacherService.getTeachersByLanguage(language);
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Get teachers with minimum rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teachers found")
    })
    @GetMapping(GET_BY_MIN_RATING)
    public ResponseEntity<ApiResp<List<TeacherResponseDto>>> getTeachersWithMinRating(
            @Parameter(description = "Minimum rating") @PathVariable Double minRating) {
        List<TeacherResponseDto> teachers = teacherService.getTeachersWithMinRating(minRating);
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Get top rated teachers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top teachers retrieved")
    })
    @GetMapping(GET_TOP_RATED)
    public ResponseEntity<ApiResp<List<TeacherResponseDto>>> getTopRatedTeachers() {
        List<TeacherResponseDto> teachers = teacherService.getTopRatedTeachers();
        return ApiResp.success(teachers);
    }

    @Operation(summary = "Update teacher rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @PatchMapping(UPDATE_RATING)
    public ResponseEntity<ApiResp<TeacherResponseDto>> updateTeacherRating(
            @Parameter(description = "Teacher ID") @PathVariable Long id,
            @Parameter(description = "New rating") @RequestParam Double rating) {
        try {
            TeacherResponseDto teacher = teacherService.updateTeacherRating(id, rating);
            return ApiResp.success(teacher);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResp.<TeacherResponseDto>builder()
                            .success(false)
                            .error(ApiResp.ErrorResp.builder()
                                    .message(e.getReason())
                                    .build())
                            .build());
        }
    }
}
