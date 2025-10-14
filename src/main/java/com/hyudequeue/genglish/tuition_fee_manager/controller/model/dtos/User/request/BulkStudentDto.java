package com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkStudentDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone must be 10-11 digits", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String phone;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;
}