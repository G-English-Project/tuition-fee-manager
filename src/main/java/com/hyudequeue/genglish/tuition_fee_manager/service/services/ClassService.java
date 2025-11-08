package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassCategoryRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassCategoryResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDtoWithCount;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.MultipleStudentAssignmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.BulkStudentCreateAndAssignDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.*;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserInClassWithNoteDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.ClassStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

public interface ClassService {
    Page<UserInClassWithNoteDto> GetCurrentStudentInClass(Long classId, int pageNumber, int pageSize);
    Page<UserInClassWithNoteDto> GetAllStudentInClass(Long classId, int pageNumber, int pageSize);
    Page<ClassResponseDtoWithCount> GetAllClasses(
            int pageNumber,
            int pageSize,
            LocalDate effectiveFrom,
            ClassStatusEnum status,
            String prioritizedCategoryName
    );

    ClassResponseDto GetClassById(Long classId);
    ClassResponseDto CreateClass(ClassRequestDto classCreate);
    ClassResponseDto EditClass(Long classId, ClassRequestDto classEdit);
    void RemoveClass(Long classId);
    ClassResponseDto ModifyClassFee(ClassFeeModifyRequestDto classFeeModify);
    Page<EnrollmentResponseDto> GetStudentEnrollmentClasses(Long studentId, int pageNumber, int pageSize);
    EnrollmentResponseDto AssignStudentToClass(Long classId, Long studentId);
    MultipleStudentAssignmentResponseDto AssignMultipleStudentsToClass(Long classId, List<Long> studentIds);
    void RemoveStudentFromClass(Long classId, Long studentId);
    UserInClassWithNoteDto NoteAStudentInClass(Long classId, Long studentId, String note);
    void RestoreClass(Long classId);
    List<ClassResponseDto> GetClassesByCategory(Long categoryId);
    BulkStudentCreateAndAssignResponseDto bulkCreateStudentsAndAssignToClass(BulkStudentCreateAndAssignDto request);
    MonthlyRevenueResponseDto GetMonthlyRevenue(
            Long classId,
            LocalDate fromDate,
            LocalDate toDate
    );
    ClassResponseDto assignMentor(Long classId, Long mentorId);
    void removeMentor(Long classId);

}
