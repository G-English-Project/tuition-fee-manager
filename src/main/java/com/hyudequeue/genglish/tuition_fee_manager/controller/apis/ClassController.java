package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.MultipleStudentAssignmentDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ClassEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.CLASS_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(CLASS_API)
public class ClassController {

    private final ClassService classService;

    @Operation(summary = "Get all classes", description = "Returns a paginated list of all classes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of classes retrieved successfully"),
    })
    @GetMapping(GET_ALL_CLASSES)
    public ResponseEntity<?> getAllClasses(
            @Parameter(description = "Page number", example = "0") @RequestParam int pageNumber,
            @Parameter(description = "Page size", example = "10") @RequestParam int pageSize) {
        return ApiResp.success(classService.GetAllClasses(pageNumber, pageSize));
    }

    @Operation(summary = "Get class by ID", description = "Returns details of a specific class by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @GetMapping(GET_CLASS_BY_ID)
    public ResponseEntity<?> getClassById(
            @Parameter(description = "ID of the class", required = true) @PathVariable Long classId) {
        return ApiResp.success(classService.GetClassById(classId));
    }

    @Operation(summary = "Create new class", description = "Creates a new class with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(CREATE_CLASS)
    public ResponseEntity<?> createClass(
            @Parameter(description = "Class creation data", required = true)
            @RequestBody ClassRequestDto request) {
        return ApiResp.success(classService.CreateClass(request));
    }

    @Operation(summary = "Edit class", description = "Updates details of an existing class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class updated successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PutMapping(EDIT_CLASS + "/{classId}")
    public ResponseEntity<?> editClass(
            @Parameter(description = "ID of the class to edit", required = true)
            @PathVariable Long classId,

            @Parameter(description = "Updated class information", required = true)
            @RequestBody ClassRequestDto request) {
        return ApiResp.success(classService.EditClass(classId, request));
    }


    @Operation(summary = "Remove class", description = "Deletes a class by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class removed successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @DeleteMapping(REMOVE_CLASS)
    public ResponseEntity<?> removeClass(
            @Parameter(description = "ID of the class to remove", required = true)
            @PathVariable Long classId) {
        classService.RemoveClass(classId);
        return ApiResp.success("Class removed successfully.");
    }

    @Operation(summary = "Modify class fee", description = "Updates the fee for a specific class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Class fee updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid fee input")
    })
    @PatchMapping(MODIFY_CLASS_FEE)
    public ResponseEntity<?> modifyClassFee(
            @Parameter(description = "Class fee modification data", required = true)
            @RequestBody ClassFeeModifyRequestDto request) {
        return ApiResp.success(classService.ModifyClassFee(request));
    }

    @Operation(summary = "Get current students in class", description = "Returns students currently enrolled in a class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current students retrieved successfully")
    })
    @GetMapping(CURRENT_STUDENTS)
    public ResponseEntity<?> getCurrentStudentsInClass(
            @Parameter(description = "ID of the class") @PathVariable Long classId,
            @Parameter(description = "Page number") @RequestParam int pageNumber,
            @Parameter(description = "Page size") @RequestParam int pageSize) {
        return ApiResp.success(classService.GetCurrentStudentInClass(classId, pageNumber, pageSize));
    }

    @Operation(summary = "Get all students in class", description = "Returns all students who have ever enrolled in a class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All students retrieved successfully")
    })
    @GetMapping(ALL_STUDENTS)
    public ResponseEntity<?> getAllStudentsInClass(
            @Parameter(description = "ID of the class") @PathVariable Long classId,
            @Parameter(description = "Page number") @RequestParam int pageNumber,
            @Parameter(description = "Page size") @RequestParam int pageSize) {
        return ApiResp.success(classService.GetAllStudentInClass(classId, pageNumber, pageSize));
    }

    @Operation(summary = "Get class enrollments by student", description = "Returns classes a student has enrolled in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully")
    })
    @GetMapping(ENROLLMENT_BY_STUDENT)
    public ResponseEntity<?> getStudentEnrollments(
            @Parameter(description = "ID of the student") @PathVariable Long studentId,
            @Parameter(description = "Page number") @RequestParam int pageNumber,
            @Parameter(description = "Page size") @RequestParam int pageSize) {
        return ApiResp.success(classService.GetStudentEnrollmentClasses(studentId, pageNumber, pageSize));
    }

    @Operation(summary = "Assign student to class", description = "Assigns a student to a class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student assigned to class successfully"),
            @ApiResponse(responseCode = "404", description = "Class or student not found")
    })
    @PostMapping(ASSIGN_STUDENT)
    public ResponseEntity<?> assignStudentToClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        return ApiResp.success(classService.AssignStudentToClass(classId, studentId));
    }

    @Operation(summary = "Assign multiple students to class", description = "Assigns multiple students to a class in a single request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students assignment process completed"),
            @ApiResponse(responseCode = "404", description = "Class not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping(ASSIGN_MULTIPLE_STUDENTS)
    public ResponseEntity<?> assignMultipleStudentsToClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Request containing list of student IDs") 
            @RequestBody MultipleStudentAssignmentDto request) {
        return ApiResp.success(classService.AssignMultipleStudentsToClass(classId, request.getStudentIds()));
    }

    @Operation(summary = "Remove student from class", description = "Removes a student from a class.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student removed from class successfully"),
            @ApiResponse(responseCode = "404", description = "Class or student not found")
    })
    @DeleteMapping(REMOVE_STUDENT)
    public ResponseEntity<?> removeStudentFromClass(
            @Parameter(description = "Class ID") @PathVariable Long classId,
            @Parameter(description = "Student ID") @PathVariable Long studentId) {
        classService.RemoveStudentFromClass(classId, studentId);
        return ApiResp.success("Student removed from class.");
    }

    @Operation(
            summary = "Update note for a student in a class",
            description = "Create or update a note for the given student in the given class."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Note updated successfully"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found or already unenrolled")
    })
    @PatchMapping(NOTE_STUDENT)
    public ResponseEntity<?> noteAStudentInClass(
            @Parameter(description = "Class ID", required = true) @PathVariable Long classId,
            @Parameter(description = "Student ID", required = true) @PathVariable Long studentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Note payload", required = true)
            @RequestBody String note
    ) {
        return ApiResp.success(classService.NoteAStudentInClass(classId, studentId, note));
    }

}
