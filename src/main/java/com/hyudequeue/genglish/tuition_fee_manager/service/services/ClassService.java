package com.hyudequeue.genglish.tuition_fee_manager.service.services;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassFeeModifyRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.request.ClassRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Classes.response.ClassResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.Enrollment.response.EnrollmentResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface ClassService {
    Page<UserResponseDto> GetCurrentStudentInClass(Long classId, int pageNumber, int pageSize);
    Page<UserResponseDto> GetAllStudentInClass(Long classId, int pageNumber, int pageSize);
    Page<ClassResponseDto> GetAllClasses(int pageNumber, int pageSize);
    ClassResponseDto GetClassById(Long classId);
    ClassResponseDto CreateClass(ClassRequestDto classCreate);
    ClassResponseDto EditClass(ClassRequestDto classEdit);
    void RemoveClass(Long classId);
    ClassResponseDto ModifyClassFee(ClassFeeModifyRequestDto classFeeModify);
    Page<EnrollmentResponseDto> GetStudentEnrollmentClasses(Long studentId, int pageNumber, int pageSize);
    EnrollmentResponseDto AssignStudentToClass(Long classId, Long studentId);
    void RemoveStudentFromClass(Long classId, Long studentId);

}
