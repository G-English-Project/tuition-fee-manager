package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserCreateRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserEditRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.UserEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.USER_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER_API)
public class UserController {
    private final UserService userService;

    @GetMapping(GET_ALL_ENDPOINT)
    public ResponseEntity<?> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResp.success(userService.GetAllStudent(page, size));
    }
    @PostMapping(CREATE_ENDPOINT)
    public ResponseEntity<?> CreateStudent(
            @RequestBody UserCreateRequestDto userCreate){
        return ApiResp.success(userService.CreateStudent(userCreate));
    }

    @PutMapping(EDIT_ENDPOINT)
    public ResponseEntity<?> EditProfile(
            @PathVariable Long userId,
            @RequestBody UserEditRequestDto userDto ){
        userDto.setUserId(userId);
        return ApiResp.success(userService.EditProfile(userDto));
    }

    @DeleteMapping(DELETE_ENDPOINT)
    public ResponseEntity<?> DeleteUser(
            @PathVariable Long userId){
        userService.DeleteStudent(userId);
        return ApiResp.success(null);
    }
}
